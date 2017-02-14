(ns picture-gallery.validation
  (:require [bouncer.core :as b]
            [bouncer.validators :as v]))

(defn registration-errors [{:keys [pass-confirm] :as params}]
  (first
    (b/validate
      params
      :id   v/required
      :pass [v/required
             [v/min-count 7 :message "password must contain at least 8 characters"]
             [= pass-confirm :message "re-entered password does not match"]])))
