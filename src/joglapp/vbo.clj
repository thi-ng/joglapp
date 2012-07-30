(ns joglapp.vbo
  (:import
    (javax.media.opengl GL GL2)
    (com.jogamp.common.nio Buffers)
    (java.nio FloatBuffer)))

(defn update-buffer
  [^GL2 gl target id data dynamic?]
  (let [fbuf (FloatBuffer/wrap (float-array data))
        _ (.rewind fbuf)
        count (.remaining fbuf)
        size (* count Buffers/SIZEOF_FLOAT)]
    (doto gl
      (.glBindBuffer target id)
      (.glBufferData target size fbuf (if dynamic? GL/GL_DYNAMIC_DRAW GL/GL_STATIC_DRAW)))
    [id count]))

(defn draw-buffer
  [^GL2 gl id count num stride draw-mode]
  (doto gl
    (.glBindBuffer GL/GL_ARRAY_BUFFER id)
    (.glEnableClientState GL2/GL_VERTEX_ARRAY)
    (.glVertexPointer num GL/GL_FLOAT (* stride Buffers/SIZEOF_FLOAT) 0)
		(.glDrawArrays draw-mode 0 count)
		(.glDisableClientState GL2/GL_VERTEX_ARRAY)
		(.glBindBuffer GL/GL_ARRAY_BUFFER 0)))

(defn make-buffer
  [^GL2 gl target data dynamic?]
  (let [buffers (int-array 1)]
    (.glGenBuffers gl 1 buffers 0)
    (update-buffer gl target (aget buffers 0) data dynamic?)))

(defn delete-buffer
  [^GL2 gl id]
  (let[idx (int-array 1)
       _ (aset idx 0 (int id))]
    (.glDeleteBuffers gl 1 idx 0)))
