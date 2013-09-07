(ns stefon.settings
  (:require [clojure.java.io :as io]
            [me.raynes.fs :as fs]
            [stefon.util :refer (dump)])
  (:import [java.io File]))

(defonce ^:dynamic *settings*
  {:asset-roots ["resources/assets"] ; returns first one it finds
   :serving-root "resources/public"
   :mode :development
   ;; you don't necessarily want this in the assets dir
   :manifest-file "resources/manifest.json"})

(defmacro with-options [options & body]
  `(binding [*settings* (merge *settings* ~options)]
     (do ~@body)))

(defn production? []
  (-> *settings* :mode (= :development) not))

(defonce tmp-dir-path-delay
  (delay (if-let [^File tmp-dir (fs/temp-dir "stefon")]
           (.getAbsolutePath tmp-dir)
           (throw (Exception. "Could not create tmp dir for serving-root.")))))

(defn serving-root
  "Determine what the serving root of the application should be. In production
   this is the serving root key of *settings*. In development it creates a
   tempory directory and uses it."
  []
  (if (production?)
    (:serving-root *settings*)
    ; It is possible, though unlikely, that creating a tmp dir will fail
    @tmp-dir-path-delay))

(defn serving-asset-root []
  (str (serving-root) "/assets"))

(defn manifest-file []
  (:manifest-file *settings*))

(defn precompiles []
  (:precompiles *settings*))

(defn asset-roots []
  (let [result  (:asset-roots *settings*)]
    (doseq [root result]
      (when-not (re-matches #".*assets.*" root)
        (throw (Exception. "Root must contain 'assets'"))))
    result))
