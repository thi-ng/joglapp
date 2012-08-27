(ns joglapp.dnd
  (:import
    [java.awt.dnd DropTarget DropTargetListener DropTargetAdapter
     DropTargetDragEvent DropTargetDropEvent DnDConstants]
    [java.awt.datatransfer Transferable DataFlavor UnsupportedFlavorException]))

(defn add-dndlistener
  [component & listeners]
  (let [{:keys [drop enter exit over action-changed]} (apply hash-map listeners)]
    (DropTarget.
      component
      (proxy [DropTargetAdapter] []
        (dragEnter [^DropTargetDragEvent e] (when enter) (enter e))
        (dragExit [^DropTargetDragEvent e] (when exit) (exit e))
        (drop [^DropTargetDropEvent e] (drop e))))))

(defn filedrop-wrapper
  "Takes a map of regexp and handlers and returns a generic file drop target listener.
  The listener preprocesses the DnD Transferable and calls handlers with any matching files.
  If there're no matching files, no handler is not invoked."
  [handlers]
  (fn [^DropTargetDropEvent e]
    (let [tx (.getTransferable e)]
      (try
        (.acceptDrop e DnDConstants/ACTION_MOVE)
        (let [paths (.getTransferData tx DataFlavor/javaFileListFlavor)]
          (doseq [[rexp handler] handlers]
            (let [matching (filter #(re-find rexp (.getName ^java.io.File %)) paths)]
              (when-not (nil? (seq matching))
                (doseq [p matching] (prn "matching file:" p))
                (handler matching)))))
          (catch UnsupportedFlavorException e (.printStackTrace e))))))
