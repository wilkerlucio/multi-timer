(ns com.wsscode.multi-timer.ui
  (:require ["react" :as react]
            ["react-native" :as rn]))

(set! js/React react)

; core

(defn wrap-component [comp]
  (fn [props & children]
    (apply react/createElement comp props children)))

(def button (wrap-component rn/Button))
(def flat-list (wrap-component rn/FlatList))
(def scroll-view (wrap-component rn/ScrollView))
(def text (wrap-component rn/Text))
(def touchable-highlight (wrap-component rn/TouchableHighlight))
(def view (wrap-component rn/View))
