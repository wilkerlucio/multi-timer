(ns com.wsscode.multi-timer.components.timer
  (:require [fulcro.client.primitives :as fp]
            [com.wsscode.multi-timer.ui :as ui]
            [clojure.spec.alpha :as s]
            [fulcro.client.mutations :as mutations]))

(s/def ::id uuid?)
(s/def ::sections (s/coll-of (s/keys)))
(s/def ::record-start pos-int?)

(defn time-now []
  (js/Math.floor (/ (.getTime (js/Date.)) 1000)))

(mutations/defmutation add-section [_]
  (action [{:keys [state ref]}]
    (let [time (- (time-now) (get-in @state (conj ref ::record-start)))]
      (swap! state update-in ref update ::sections conj {::section-id (random-uuid)
                                                         ::time time}))))

(fp/defsc Timer
  [this props]
  {:ident [::id ::id]
   :query [::id]}
  (ui/view (clj->js {})
    ))

(def timer (fp/factory Timer {:keyfn ::id}))

(fp/defsc RecorderTimeSection
  [this {::keys [time]}]
  {:initial-state {}
   :ident         [::section-id ::section-id]
   :query         [::section-id ::time ::action]}
  (ui/view nil
    (ui/text #js {:style #js {:color "#000"}} (str time))))

(def recorder-time-section (fp/factory RecorderTimeSection {:keyfn ::section-id}))

(fp/defsc Recorder
  [this {::keys [current-time record-start sections]}]
  {:initial-state {::id           (random-uuid)
                   ::sections     []
                   ::record-start nil}
   :ident         [::id ::id]
   :query         [::id ::sections ::record-start [::current-time '_]]}
  (ui/view nil
    (mapv recorder-time-section sections)
    (if record-start
      (ui/view nil
        (ui/text #js {:style #js {:color "#000"}} (str "Running clock!! " (js/Math.max 0 (- current-time record-start))))
        (ui/button #js {:onPress #(fp/transact! this [`(add-section {})])
                        :title "Add time point"}))
      (ui/touchable-highlight #js {:onPress #(mutations/set-value! this ::record-start (time-now))}
        (ui/text #js {:style #js {:color "#000"}} "Start")))))

(def recorder (fp/factory Recorder {:keyfn ::id}))

[{::time 3.5
  ::action "Colocar no fogo"}
 {::time 6.3
  ::action "Trocar X"}
 {::time 9
  ::action "Bla"}]
