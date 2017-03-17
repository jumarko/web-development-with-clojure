(ns swagger-service.doo-runner
  (:require [doo.runner :refer-macros [doo-tests]]
            [swagger-service.core-test]))

(doo-tests 'swagger-service.core-test)

