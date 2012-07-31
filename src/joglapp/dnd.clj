(ns joglapp.dnd
  (:import
    [java.awt.dnd DropTarget DropTargetListener DropTargetAdapter DropTargetDragEvent DropTargetDropEvent DnDConstants]
    [java.awt.datatransfer Transferable DataFlavor UnsupportedFlavorException]))

(defn add-dndlistener
  [component & listeners]
  (let[{:keys[drop enter exit over action-changed]} (apply hash-map listeners)]
    (DropTarget.
      component
      (proxy [DropTargetAdapter] []
        (dragEnter [^DropTargetDragEvent e] (when enter) (enter e))
        (dragExit [^DropTargetDragEvent e] (when exit) (exit e))
        (drop [^DropTargetDropEvent e] (drop e))))))

(defn filedrop-wrapper
  "Wraps a given drop target listener function to preprocess the DnD Transferable
  and filters out any non-matching file types using given reg-exp. The listener
  is only invoked once and passed the filtered file collection (if any).
  If there're no matching files, the listener is not invoked.

  The wrapper is meant to be used as partial."
  [rexp listener ^DropTargetDropEvent e]
  (let [tx (.getTransferable e)]
    (try
      (.acceptDrop e DnDConstants/ACTION_MOVE)
      (let[paths (filter #(re-find rexp (.getName %)) (.getTransferData tx DataFlavor/javaFileListFlavor))]
        (when-not (nil? (seq paths))
          (doseq[p paths] (prn "file received:" p))
          (listener paths)))
      (catch UnsupportedFlavorException e (.printStackTrace e)))))
