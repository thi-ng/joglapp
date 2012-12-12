(ns joglapp.tiler
  (:require
    [toxi.math.core :as m]
    [toxi.math.matrix :as mat]
    [clojure.java.io :as io :only [output-stream]])
  (:import
    [java.awt.image BufferedImage]
    [javax.imageio ImageIO]
    [javax.media.opengl GL2 GLAutoDrawable]
    [com.jogamp.opengl.util.awt Screenshot]))

(defn subdiv-rect
  [{:keys [left right top bottom]} num]
  (for [y (range num) x (range num)]
    [[x (- num 1 y)] {:left (m/map-interval x 0 num left right)
            :right (m/map-interval (inc x) 0 num left right)
            :bottom (m/map-interval y 0 num bottom top)
            :top (m/map-interval (inc y) 0 num bottom top)}]))

(defn ^BufferedImage make-image
  [width height]
  (BufferedImage. width height BufferedImage/TYPE_4BYTE_ABGR))

(defn record-tile!
  [x y w h ^BufferedImage img]
  (let [^BufferedImage tile (Screenshot/readToBufferedImage w h)
        rgb (.getRGB tile 0 0 w h nil 0 w)]
    (.setRGB img (int (* x w)) (int (* y h)) w h rgb 0 w)))

(defn ^BufferedImage render-tiles
  [^GLAutoDrawable drawable render-fn frustum near far num]
  (let [w (.getWidth drawable)
        h (.getHeight drawable)
        ^BufferedImage img (make-image (* w num) (* h num))]
    (doseq [[[x y] {fl :left fr :right ft :top fb :bottom}] (subdiv-rect frustum num)]
      (render-fn
        drawable
        (-> (mat/frustum fl ft fr fb near far) mat/transpose double-array))
      (record-tile! x y w h img))
    img))

(defn save-image
  [^BufferedImage img dest]
  (with-open [stream (io/output-stream dest)]
    (ImageIO/write img "PNG" stream)))
