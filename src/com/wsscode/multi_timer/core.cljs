(ns com.wsscode.multi-timer.core
  (:require ["react" :as react]
            [goog.object :as gobj]
            [com.wsscode.multi-timer.ui :as ui]
            [com.wsscode.multi-timer.rn-support :as support]
            [com.wsscode.multi-timer.components.timer :as c.timer]
            [fulcro.client :as fulcro]
            [fulcro.client.mutations :as mutations]
            [fulcro.client.primitives :as fp]))

(mutations/defmutation tick-clock [{::c.timer/keys [current-time]}]
  (action [{:keys [state]}]
    (swap! state assoc ::c.timer/current-time current-time))
  (refresh [_] [::c.timer/current-time]))

(fp/defsc MultiTimer
  [this {::keys [id recorder]}]
  {:initial-state (fn [_] {::id       (random-uuid)
                           ::recorder (fp/get-initial-state c.timer/Recorder {})})
   :ident         [::id ::id]
   :query         [::id
                   {::recorder (fp/get-query c.timer/Recorder)}]}
  (ui/view (clj->js {:style {:flex            1
                             :backgroundColor "#fff"
                             :alignItems      "center"
                             :justifyContent  "center"}})
    (c.timer/recorder recorder)))

(def multi-timer (fp/factory MultiTimer))

(fp/defsc Root [_ {:keys [ui/root]}]
  {:initial-state {:ui/root               {}
                   ::c.timer/current-time 100}
   :query         [{:ui/root (fp/get-query MultiTimer)} ::c.timer/current-time]}
  (multi-timer root))

(defn tick-timer [reconciler]
  (let [time (c.timer/time-now)]
    (fp/compressible-transact! reconciler [`(tick-clock {::c.timer/current-time ~time})])
    (js/requestAnimationFrame #(tick-timer reconciler))))

(defonce app
  (atom
    (fulcro/new-fulcro-client
      :started-callback
      (fn [app]
        (js/setInterval
          (fn []
            (let [time (c.timer/time-now)]
              (fp/compressible-transact! (:reconciler app) [`(tick-clock {::c.timer/current-time ~time})])))
          500)
        #_ (tick-timer (:reconciler app)))
      :shared {::time-based (atom #{})}
      :reconciler-options {:root-render  support/root-render
                           :root-unmount support/root-unmount})))

(defonce RootNode (support/root-node! 1))
(defonce app-root (fp/factory RootNode))

(defn reload []
  (swap! app fulcro/mount Root 1))

(defn rootelement []
  (reload)
  (app-root {}))
