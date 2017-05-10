(ns picture-gallery.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [picture-gallery.core-test]))

(doo-tests 'picture-gallery.core-test)

