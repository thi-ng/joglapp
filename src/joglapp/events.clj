(ns joglapp.events
	(:import
		(java.awt Component)
		(java.awt.event KeyAdapter KeyEvent
                  MouseAdapter MouseEvent
                  MouseWheelListener MouseWheelEvent)))

(defn make-mouse-state [] (atom {:x 0 :y 0 :buttons #{}}))

(defn mouse-moved
  [{:keys[x y]} state]
  (swap! state assoc :x x :y y))

(defn mouse-pressed
	[{:keys[x y button]} state]
	(swap! state assoc :x x :y y :buttons (conj (:buttons @state) (get [:left :middle :right] (dec button)))))

(defn mouse-released
	[{:keys[x y button]} state]
	(swap! state assoc :x x :y y :buttons (disj (:buttons @state) (get [:left :middle :right] (dec button)))))

(defn add-mouselisteners
  [^Component component & state-listeners]
  (let [{:keys[state moved dragged pressed released wheel]
         :or {state (make-mouse-state)}} (apply hash-map state-listeners)
        motion-proxy (proxy [MouseAdapter] []
                       (mouseMoved [^MouseEvent e]
                                   (if-not (nil? moved) (moved (bean e) state)))
                       (mouseDragged [^MouseEvent e]
                                     (if-not (nil? dragged) (dragged (bean e) state))))
        mouse-proxy (proxy [MouseAdapter] []
                      (mousePressed [^MouseEvent e]
                                    (if-not (nil? pressed) (pressed (bean e) state)))
                      (mouseReleased [^MouseEvent e]
                                     (if-not (nil? released) (released (bean e) state))))
        wheel-proxy (proxy [MouseWheelListener] []
                      (mouseWheelMoved [^MouseWheelEvent e]
                                       (wheel (.getWheelRotation e) state)))]
    (.addMouseMotionListener component motion-proxy)
    (.addMouseListener component mouse-proxy)
    (if wheel
      (do
        (.addMouseWheelListener component wheel-proxy)
        (swap! state assoc :mouse-listeners {:mouse-motion motion-proxy
                                             :mouse mouse-proxy
                                             :wheel wheel-proxy}))
      (swap! state assoc :mouse-listeners {:mouse-motion motion-proxy :mouse mouse-proxy}))
    state))

(defn remove-mouselisteners
  [^Component component & listeners]
  (let [{:keys [mouse-motion mouse wheel]} (if (map? (first listeners)) (first listeners) (apply hash-map listeners))]
    (when mouse-motion (.removeMouseMotionListener component mouse-motion))
    (when mouse (.removeMouseListener component mouse))
    (when wheel (.removeMouseWheelListener component wheel))))

(defn add-keylisteners
  [^Component component & state-listeners]
  (let [{:keys[state pressed released] :or {state (atom {})}} (apply hash-map state-listeners)
        key-proxy (proxy [KeyAdapter] []
                    (keyPressed [^KeyEvent e]
                                (if-not (nil? pressed) (pressed (bean e) state)))
                    (keyReleased [^KeyEvent e]
                                 (if-not (nil? released) (released (bean e) state))))]
    (.addKeyListener component key-proxy)
    (swap! state assoc :key-listener key-proxy)
    state))
