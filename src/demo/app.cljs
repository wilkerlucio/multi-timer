(ns demo.app
  (:require ["react" :as react :refer [createElement]]
            ["react-native" :as rn :refer [Text View StyleSheet]]
            [fulcro.client :as fulcro]
            [fulcro.client.primitives :as fp]))

(set! js/React react)

(defn wrap-component [comp]
  (fn [props & children]
    (apply createElement comp (clj->js props) children)))

(def view (wrap-component View))
(def text (wrap-component Text))

(def styles
  {:container
   {:flex            1
    :backgroundColor "#fff"
    :alignItems      "center"
    :justifyContent  "center"}})

(fp/defsc MultiTimer
  [this {::keys [id]}]
  {:initial-state (fn [_] {::id (random-uuid)})
   :ident         [::id ::id]
   :query         [::id]}
  (view {:style (:container styles)}
    (text nil "Hello from Nosso Timer app from Fulcro!!!")))

(def multi-timer (fp/factory MultiTimer))

(fp/defsc Root [_ {:keys [ui/root]}]
  {:initial-state {:ui/root {}}
   :query         [{:ui/root (fp/get-query MultiTimer)}]}
  (multi-timer root))

(defn ^:export foo []
  (multi-timer {}))
