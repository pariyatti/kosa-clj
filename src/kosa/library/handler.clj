(ns kosa.library.handler
  (:require [kosa.library.views :as views]
            [ring.util.response :as resp]))

(defn index [request]
  (resp/response
   (views/index request)))
