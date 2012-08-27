(ns joglapp.core
  (:import
    [javax.media.opengl GL GL2 GLProfile GLCapabilities GLEventListener
     GLAutoDrawable DefaultGLCapabilitiesChooser]
    [javax.media.opengl.awt GLCanvas]
    [javax.media.opengl.fixedfunc GLMatrixFunc]
    [com.jogamp.opengl.util FPSAnimator]
    [com.jogamp.opengl.util.awt Screenshot TextRenderer]
    [com.jogamp.opengl.util.texture Texture]
    [com.jogamp.opengl.util.texture.awt AWTTextureIO]
    [java.awt Frame Font]
    [java.awt.image BufferedImage])
  (:require
    [toxi.math.core :as math]
    [toxi.math.matrix4x4 :as mat4]))

(defn ^GLCanvas make-canvas
  ([]
    (make-canvas GLProfile/GL2))
  ([profile]
    (let [glcaps (GLCapabilities. (GLProfile/get profile))]
      (doto ^GLCapabilities glcaps
        (.setDoubleBuffered true)
        (.setHardwareAccelerated true)
        (.setSampleBuffers true)
        (.setNumSamples 4))
      (GLCanvas. glcaps (DefaultGLCapabilitiesChooser.) nil nil))))

(defn ^Frame make-frame
  [& opts]
  (let [{:keys [title width height chrome canvas]
        :or {title "joglapp" width 1280 height 720 chrome true}} (apply hash-map opts)
       frame (Frame. ^String title)]
    (.setSize frame width height)
    (.add frame ^GLCanvas canvas)
    (when-not chrome (.setUndecorated frame true))
    (.show frame)
    frame))

(defn ^Texture make-image-texture
  ([^BufferedImage img]
    (make-image-texture img GLProfile/GL2))
  ([^BufferedImage img glprofile]
    (AWTTextureIO/newTexture (GLProfile/get glprofile) img false)))

(defn setup
  [& more]
  (let [{:keys [canvas frame width height fps init dispose display reshape]
         :or {fps 60}} (apply hash-map more)
        ^GLCanvas canvas (if (nil? canvas) (make-canvas GLProfile/GL2) canvas)
        frame (if (nil? frame) (make-frame :width width :height height) frame)
        anim (FPSAnimator. canvas (int fps))
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

(defn view-perspective
  ([^GLAutoDrawable drawable fov near far eye target up]
    (view-perspective
      drawable
      (-> (mat4/perspective fov (/ (.getWidth drawable) (.getHeight drawable)) near far)
        math/matrix-transpose
        mat4/->array)
      (-> (mat4/look-at eye target up)
        math/matrix-transpose
        mat4/->array)))
  ([^GLAutoDrawable drawable frustum lookat]
    (doto ^GL2 (.. drawable getGL getGL2)
      (.glViewport 0 0 (.getWidth drawable) (.getHeight drawable))
      (.glMatrixMode GLMatrixFunc/GL_PROJECTION)
      (.glLoadMatrixd frustum 0)
      (.glMatrixMode GLMatrixFunc/GL_MODELVIEW)
      (.glLoadMatrixd lookat 0))
    [frustum lookat]))

(defn view-ortho2d
  ([^GLAutoDrawable drawable]
    (view-ortho2d drawable (mat4/->array mat4/M4X4-IDENTITY)))
  ([^GLAutoDrawable drawable lookat]
    (let [^GL2 gl (.. drawable getGL getGL2)
         w (.getWidth drawable)
         h (.getHeight drawable)
         frustum (-> (mat4/ortho 0 0 w h -1 1) math/matrix-transpose mat4/->array)]
      (doto gl
        (.glMatrixMode GLMatrixFunc/GL_PROJECTION)
        (.glLoadMatrixd frustum 0)
        (.glMatrixMode GLMatrixFunc/GL_MODELVIEW)
        (.glLoadMatrixd lookat 0))
      [frustum lookat])))

(def blend-modes
  {:alpha [GL/GL_SRC_ALPHA GL/GL_ONE_MINUS_SRC_ALPHA GL/GL_FUNC_ADD]
   :add [GL/GL_SRC_ALPHA GL/GL_ONE GL/GL_FUNC_ADD]
   :sub-alt [GL/GL_DST_COLOR GL/GL_ONE_MINUS_SRC_ALPHA GL/GL_FUNC_ADD]
   :sub [GL/GL_SRC_ALPHA GL/GL_ONE GL/GL_FUNC_REVERSE_SUBTRACT]})

(defn apply-blend-mode
  [^GL2 gl id]
  (let [[blend-src blend-dest blend-eq] (get blend-modes id)]
    (doto gl
      (.glEnable GL/GL_BLEND)
      (.glBlendFunc blend-src blend-dest)
      (.glBlendEquation blend-eq))))

(defn draw-origin
  [^GL2 gl len]
  (.glBegin gl GL2/GL_LINES)
  (doseq [[^double r ^double g ^double b] [[1 0 0] [0 1 0] [0 0 1]]]
    (.glColor3d gl r g b)
    (.glVertex3d gl 0 0 0)
    (.glVertex3d gl (* r len) (* g len) (* b len)))
  (.glEnd gl))
