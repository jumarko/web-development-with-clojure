(ns appendix1)

(declare ^:dynamic *foo*)

(println *foo*)

(binding [*foo* "I exist!"]
  (println *foo*))

;;; Polymorphism

;; Multimethods
(defmulti area :shape)

(defmethod area :circle [{:keys [r]}]
  (* Math/PI r r))

(defmethod area :reactangle [{:keys [l w]}]
  (* l w))

(defmethod area :default [shape]
  (throw (Exception. (str "unrecognized shape: " shape))))

(area {:shape :circle :r 10})
(area {:shape :reactangle :l 100 :w 20})
(area {:shape :unknown :r 10})

;; more sophisticated dispatch function
(defmulti encounter
  (fn [x y] [(:role x) (:role y)]))

(defmethod encounter [:manager :boss] [x y]
  :promise-unrealistic-deadlines)

(defmethod encounter [:manager :developer] [x y]
  :demand-overtime)

(defmethod encounter [:developer :developer] [x y]
  :complain-about-poor-management)

(encounter {:role :manager} {:role :boss})
(encounter {:role :manager} {:role :developer})
(encounter {:role :developer} {:role :developer})
(encounter {:role :developer} {:role :manager})
(encounter {:role :boss} {:role :manager})

;; protocols
(defprotocol Foo
  "Foo doc string"
  (bar [this b] "bar doc string")
  (baz [this] [this b] "baz doc string"))

(deftype Bar [data]
  Foo
  (bar [this param]
    (println data param))
  (baz [this]
    (println (class this)))
  (baz [this param]
    (println param)))

(let [b (Bar. "some data")]
  (.bar b "param")
  (.baz b)
  (.baz b "baz with param"))

(extend-protocol Foo String
                 (bar [this param] (println this param)))

(bar "Hello" "World")
(baz "Hello" "World")

;;; Global state

;; atom
(def global-val (atom nil))

(println (deref global-val))
(println @global-val)

(reset! global-val 10)
(println @global-val)

(swap! global-val inc)
(println @global-val)

;; ref
(def names (ref []))

(dosync
  (ref-set names ["John"])
  (alter names #(if (not-empty %)
                  (conj % "Jane")
                  %)))



;;; Macros
(def session (atom {:user "Bog"}))

(defn load-content []
  (if (:user @session)
    "Welcome back!"
    "please log in"))

(defmacro defprivate [name args & body]
  `(defn ~(symbol name) ~args
     (if (:user @session)
       (do ~@body)
       "please log in")))

(defprivate show-sth [name age]
  (println "Hello" name)
  (map #(* % %) [age age]))

(macroexpand-1 '(defprivate show-sth [name age]
                  (println "Hello" name)
                  (map #(* % %) [age age])))

(show-sth "juraj" 31)


