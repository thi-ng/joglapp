(ns joglapp.utils
  (:import
   [clojure.lang Reflector]
   [java.beans PropertyDescriptor]))

(defn select-bean-props
  [obj & props]
  (let [props (into #{}  props)
        c (.getClass ^Object obj)]
    (reduce (fn [m ^PropertyDescriptor pd]
              (let [name (keyword (.getName pd))
                    method (.getReadMethod pd)]
                (if (and method (props name) (zero? (alength (.getParameterTypes method))))
                  (assoc m name (Reflector/prepRet (.getPropertyType pd) (. method (invoke obj nil))))
                  m)))
            {}
            (seq (.. java.beans.Introspector
                     (getBeanInfo c)
                     (getPropertyDescriptors))))))
