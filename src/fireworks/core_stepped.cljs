(ns fireworks.core_stepped
  (:require [impi.core :as impi]))

(enable-console-print!)

(defmethod impi/update-prop! :pixi.sprite/tint [object _ _ tint]
  (set! (.-tint object) tint))

(def canvas-size 600)

(def canvas
  {:pixi.renderer/size             [canvas-size canvas-size]
   :pixi.renderer/background-color 0x0a1c5e
   :pixi.renderer/transparent?     false})

(def clj-melb-logo
  {:impi/key             "logo"
   :pixi.object/type     :pixi.object.type/sprite
   :pixi.object/position (repeat 2 (/ canvas-size 2))
   :pixi.sprite/anchor   [0.5 0.5]
   :pixi.sprite/texture  {:pixi.texture/source "img/clj-melb-logo.png"}})

(defn spark [id pos tint]
  {:impi/key             id
   :pixi.object/type     :pixi.object.type/sprite
   :pixi.object/position pos
   :pixi.sprite/anchor   [0.5 0.5]
   :pixi.sprite/tint     tint
   :pixi.sprite/texture  {:pixi.texture/source "img/spark.png"}})

(defn velocity [speed n-sparks nth-spark]
  [(* speed (Math/cos (* 2 Math/PI (/ nth-spark n-sparks))))
   (* speed (Math/sin (* 2 Math/PI (/ nth-spark n-sparks))))])

(defn firework [id n-sparks pos speed tint]
  {:impi/key                (keyword (str "firework_" id))
   :firework/id             id
   :pixi.object/type        :pixi.object.type/container
   :pixi.container/children (mapv #(spark (str "firework_" id "_particle_" %)
                                          (mapv + pos (velocity speed n-sparks %))
                                          tint)
                                  (range n-sparks))})

(def stage
  {:impi/key                :stage
   :pixi.object/type        :pixi.object.type/container
   :pixi.container/children [clj-melb-logo
                             (let [tint (* (Math/random) 0xFFFFFF)]
                               (firework "id" 8 [300 150] 50 tint))]})

(defonce state (atom nil))

(defn init-state! []
  (reset! state {:pixi/renderer canvas
                 :pixi/stage    stage}))

(defn mount-state! []
  (let [element (.getElementById js/document "app")]
    (impi/mount :fireworks @state element)
    (add-watch state ::mount (fn [_ _ _ s] (impi/mount :fireworks s element)))))

(do (init-state!)
    (mount-state!))
