(ns joglapp.core
  (:import
		(javax.media.opengl GL GL2 GLProfile GLCapabilities GLEventListener GLAutoDrawable DefaultGLCapabilitiesChooser)
		(javax.media.opengl.awt GLCanvas)
		(javax.media.opengl.fixedfunc GLMatrixFunc)
		(com.jogamp.opengl.util FPSAnimator)
		(com.jogamp.opengl.util.awt Screenshot TextRenderer)
		(java.awt Frame Font))
  (:require
    [toxi.math.core :as math]
    [toxi.math.matrix4x4 :as mat4]))

(defn make-canvas
  [profile]
  (let [glcaps (GLCapabilities. (GLProfile/get profile))]
    (doto ^GLCapabilities glcaps
      (.setDoubleBuffered true)
      (.setHardwareAccelerated true)
      (.setSampleBuffers true)
      (.setNumSamples 4))
    (GLCanvas. glcaps (DefaultGLCapabilitiesChooser.) nil nil)))

(defn make-frame
  [& opts]
  (let[{:keys[title width height chrome canvas]
        :or [title "joglapp" width 1280 height 720 chrome true]} (apply hash-map opts)
       frame (Frame. title)]
    (.setSize frame width height)
    (.add frame canvas)
    (when-not chrome (.setUndecorated frame true))
    (.show frame)
    frame))

(defn setup
  [& more]
  (let [{:keys[canvas frame width height fps init dispose display reshape]
         :or {fps 60}} (apply hash-map more)
        ^GLAutoDrawable canvas (if (nil? canvas) (make-canvas GLProfile/GL2) canvas)
        frame (if (nil? frame) (make-frame :width width :height height) frame)
        anim (FPSAnimator. canvas fps)
        t0 (System/currentTimeMillis)]
    (.addGLEventListener canvas
      (proxy [GLEventListener] []
        (init [drawable] (when init (init drawable)))
        (dispose [drawable] (when dispose (dispose drawable)))
        (reshape [drawable x y width height] (when reshape (reshape drawable)))
        (display [drawable] (display drawable (* (- (System/currentTimeMillis) t0) 0.001)))))
    (.requestFocus canvas)
    (.add anim canvas)
    (.start anim)
    {:frame frame :canvas canvas :anim anim}))

(defn view-ortho
  [^GLAutoDrawable drawable]
  (let[^GL2 gl (.. drawable getGL getGL2)
       w (.getWidth drawable)
       h (.getHeight drawable)]
    (doto gl
      (.glMatrixMode GLMatrixFunc/GL_PROJECTION)
      (.glLoadMatrixd (-> (mat4/ortho 0 0 w h -1 1) math/matrix-transpose mat4/->array) 0)
      (.glMatrixMode GLMatrixFunc/GL_MODELVIEW)
      (.glLoadIdentity))))
