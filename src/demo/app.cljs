(ns demo.app
  (:require ["react" :as react :refer [createElement]]
            ["react-native" :as rn :refer [Text View Button]]
            [goog.object :as  gobj]
            [fulcro.client :as fulcro]
            [fulcro.client.mutations :as mutations]
            [fulcro.client.primitives :as fp]))

(defonce root-nodes (atom {}))

(defn root-node!
  "A substitute for a real root node (1) for mounting om-next component.
  You have to call function :on-render and :on-unmount in reconciler :root-render :root-unmount function."
  [id]
  (let [content (atom nil)
        instance (atom nil)
        class (fp/ui Object
                (componentWillMount [this] (reset! instance this))
                (render [_] @content))]
    (swap! root-nodes assoc id {:on-render  (fn [el]
                                              (reset! content el)
                                              (when @instance
                                                (.forceUpdate @instance)))
                                :on-unmount (fn [])
                                :class      class})
    class))

(defn root-render
  "Use this as reconciler :root-render function."
  [el id]
  (let [node (get @root-nodes id)
        on-render (:on-render node)]
    (when on-render (on-render el))))

(defn root-unmount
  "Use this as reconciler :root-unmount function."
  [id]
  (let [node (get @root-nodes id)
        unmount-fn (:on-unmount node)]
    (when unmount-fn (unmount-fn))))

(set! js/React react)

(defn wrap-component [comp]
  (fn [props & children]
    (apply createElement comp props children)))

(def view (wrap-component View))
(def button (wrap-component Button))
(def text (wrap-component Text))

(fp/defsc MultiTimer
  [this {::keys [id counter]}]
  {:initial-state (fn [_] {::id (random-uuid)
                           ::counter 0})
   :ident         [::id ::id]
   :query         [::id ::counter]}
  (view (clj->js {:style {:flex            1
                          :backgroundColor "#fff"
                          :alignItems      "center"
                          :justifyContent  "center"}})
    (text nil (str "Welcome Other line!! " id))
    (button #js {:onPress #(mutations/set-value! this ::counter (inc counter))
                 :title   (str "Inc Counter (" counter ")")})))

(def multi-timer (fp/factory MultiTimer))

(fp/defsc Root [_ {:keys [ui/root]}]
  {:initial-state {:ui/root {}}
   :query         [{:ui/root (fp/get-query MultiTimer)}]}
  (multi-timer root))

(defonce app
  (atom
    (fulcro/new-fulcro-client
      :reconciler-options {:root-render  root-render
                           :root-unmount root-unmount})))

(defonce RootNode (root-node! 1))
(defonce app-root (fp/factory RootNode))

(defn reload []
  (swap! app fulcro/mount Root 1))

(defn rootelement []
  (reload)
  (app-root {}))
