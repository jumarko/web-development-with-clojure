;---
; Excerpted from "Web Development with Clojure, Second Edition",
; published by The Pragmatic Bookshelf.
; Copyrights apply to this code. It may not be used to create training material,
; courses, books, articles, and the like. Contact us if you are in doubt.
; We make no guarantees that this code is fit for any purpose.
; Visit http://www.pragmaticprogrammer.com/titles/dswdcloj2 for more book information.
;---
(ns picture-gallery.routes.services.upload
  (:require [picture-gallery.db.core :as db]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log])
  (:import [java.awt.image AffineTransformOp BufferedImage]
           [java.io ByteArrayOutputStream FileInputStream]
           java.awt.geom.AffineTransform
           javax.imageio.ImageIO
           java.net.URLEncoder))

(def thumb-size 150)

(def thumb-prefix "thumb_")

(defn scale [img ratio width height]
  (let [scale (AffineTransform/getScaleInstance
                (double ratio) (double ratio))

        transform-op (AffineTransformOp.
                       scale AffineTransformOp/TYPE_BILINEAR)]
    (.filter transform-op img (BufferedImage. width height (.getType img)))))

(defn scale-image [file thumb-size]
  (let [img (ImageIO/read file)
        img-width (.getWidth img)
        img-height (.getHeight img)
        ratio (/ thumb-size img-height)]
    (scale img ratio (int (* img-width ratio)) thumb-size)))

(defn image->byte-array [image]
  (let [baos (ByteArrayOutputStream.)]
    (ImageIO/write image "png" baos)
    (.toByteArray baos)))

(defn file->byte-array [x]
  (with-open [input ( FileInputStream. x)
              buffer (ByteArrayOutputStream.)]
    (clojure.java.io/copy input buffer)
    (.toByteArray buffer)))

(defn url-encode [s]
  (URLEncoder/encode s "UTF-8"))

(defn save-image! [user {:keys [tempfile filename content-type]}]
  (try
    (let [db-file-name (str user (.replaceAll filename "[^a-zA-Z0-9-_\\.]" ""))]
      (db/save-file! {:owner user
                      :type  content-type
                      :name  db-file-name
                      :data  (file->byte-array tempfile)})
      (db/save-file! {:owner user
                      :type  "image/png"
                      :data  (image->byte-array
                               (scale-image tempfile thumb-size))
                      :name  (str thumb-prefix db-file-name)}))
    (ok {:result :ok})
    (catch Exception e
      (log/error e)
      (internal-server-error "error"))))
