(ns joglapp.utils)

(defn select-bean-props
  [obj & props]
  (let [props (apply hash-set props)
       c (.getClass ^Object obj)]
    (reduce (fn [m ^java.beans.PropertyDescriptor pd]
              (let [name (keyword (.getName pd))
                    method (.getReadMethod pd)]
                (if (and method (props name) (zero? (alength (.getParameterTypes method))))
                  (assoc m name (clojure.lang.Reflector/prepRet (.getPropertyType pd) (. method (invoke obj nil))))
                  m)))
            {}
            (seq (.. java.beans.Introspector
                   (getBeanInfo c)
                   (getPropertyDescriptors))))))
