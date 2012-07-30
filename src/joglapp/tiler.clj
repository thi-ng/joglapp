(ns joglapp.tiler
  (:require
    [toxi.math.core :as math]
    [toxi.math.matrix4x4 :as mat4]
    [clojure.java.io :as io :only [file]])
  (:import
    (java.awt.image BufferedImage)
    (javax.imageio ImageIO)
    (javax.media.opengl GL2 GLAutoDrawable)
    (com.jogamp.opengl.util.awt Screenshot)))

(defn subdiv-rect
  [{:keys [left right top bottom]} num]
  (for [y (range num) x (range num)]
    [[x (- num 1 y)] {:left (math/map-interval x 0 num left right)
            :right (math/map-interval (inc x) 0 num left right)
            :bottom (math/map-interval y 0 num bottom top)
            :top (math/map-interval (inc y) 0 num bottom top)}]))

(defn make-image
  [width height]
  (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR))

(defn record-tile!
  [x y w h img]
  (let [tile (Screenshot/readToBufferedImage w h)
        rgb (.getRGB tile 0 0 w h nil 0 w)]
    (.setRGB img (* x w) (* y h) w h rgb 0 w)))

(defn render-tiles
  [^GLAutoDrawable drawable render-fn frustum near far num]
  (let [w (.getWidth drawable)
        h (.getHeight drawable)
        img (make-image (* w num) (* h num))]
    (doseq [[[x y] {fl :left fr :right ft :top fb :bottom}] (subdiv-rect frustum num)]
      (render-fn
        drawable
        (-> (mat4/frustum fl ft fr fb near far) math/matrix-transpose mat4/->array))
      (record-tile! x y w h img))
    img))

(defn save-image
  [img dest]
  (ImageIO/write img "PNG" (io/file dest)))
