(ns ^:figwheel-no-load picture-gallery.app
  (:require [picture-gallery.core :as core]
            [devtools.core :as devtools]))

(enable-console-print!)

(devtools/install!)

(core/init!)
