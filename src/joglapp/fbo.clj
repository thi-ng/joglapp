(ns joglapp.fbo
  (:import
   [javax.media.opengl GL GL2]
   [com.jogamp.opengl
    FBObject FBObject$Attachment$Type
    FBObject$TextureAttachment]))

(defn make-fbo
  [^GL gl width height tex depth?]
  (let [^FBObject fbo (FBObject.)]
    (.reset fbo gl width height)
    (doseq [[i alpha] (zipmap (range) tex)]
      (.attachTexture2D fbo gl (int i) alpha))
    (when depth? (.attachRenderBuffer fbo gl FBObject$Attachment$Type/DEPTH 32))
    (.unbind fbo gl)
    fbo))

(defn bind
  [^GL gl ^FBObject fbo]
  (.bind fbo gl)
  fbo)

(defn unbind
  [^GL gl ^FBObject fbo]
  (.unbind fbo gl)
  fbo)

(defn bind-as-texture
  [^GL gl ^FBObject fbo attachment-id tex-id]
  (.glActiveTexture gl (int tex-id))
  (.use fbo gl ^TextureAttachment (.getColorbuffer fbo (int attachment-id)))
  fbo)

(defn unbind-as-texture
  [^GL gl ^FBObject fbo]
  (.unuse fbo gl)
  fbo)
