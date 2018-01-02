(ns fireworks.core
  (:require [impi.core :as impi]))

(enable-console-print!)

(def canvas-size 600)

(defmethod impi/update-prop! :pixi.sprite/tint [object _ _ tint]
  (set! (.-tint object) tint))

(defn velocity [speed n-sparks nth-spark]
  [(* speed (Math/cos (* 2 Math/PI (/ nth-spark n-sparks))))
   (* speed (Math/sin (* 2 Math/PI (/ nth-spark n-sparks))))])

(defn create-firework [id n-sparks pos speed]
  {:impi/key         (keyword (str "firework_" id))
   :firework/id      id
   :pixi.object/type :pixi.object.type/container
   :pixi.container/children
   (let [tint (* (Math/random) 0xFFFFFF)]
     (mapv (fn [i]
             {:impi/key             (keyword (str "firework_" id "_particle_" i))
              :pixi.object/type     :pixi.object.type/sprite
              :pixi.object/position pos
              :pixi.object/rotation 0.0
              :pixi.object/alpha    1.0
              :spark/velocity       (velocity speed n-sparks i)
              :spark/speed          speed
              :pixi.sprite/anchor   [0.5 0.5]
              :pixi.sprite/tint     tint
              :pixi.sprite/texture  {:pixi.texture/source "img/spark.png"}})
           (range n-sparks)))})

(defn create-firework! [id]
  (let [n-sparks (+ 6 (rand-int 16))
        pos      [(rand-int canvas-size) (rand-int canvas-size)]
        speed    (+ 0.3 (rand 5.0))]
    (create-firework id n-sparks pos speed)))

(defn update-spark [spark]
  (-> spark
      (update :pixi.object/position #(map + % (:spark/velocity spark)))
      (update :pixi.object/alpha - (/ 0.05 (:spark/speed spark)))))

(defn update-children [container update-fn]
  (update container :pixi.container/children #(map update-fn %)))

(defn update-firework [firework]
  (if (<= (-> firework :pixi.container/children first :pixi.object/alpha) 0.0)
    (create-firework! (:firework/id firework))
    (update-children firework update-spark)))

(defn update-fireworks [state]
  (update-in state
             [:pixi/stage :pixi.container/children :fireworks]
             update-children
             update-firework))

(defonce state (atom nil))

(defn init-stage! []
  (reset!
      state
      {:pixi/renderer
       {:pixi.renderer/size             [canvas-size canvas-size]
        :pixi.renderer/background-color 0x0a1c5e
        :pixi.renderer/transparent?     false}
       :pixi/listeners {}
       :pixi/stage
       {:impi/key         :stage
        :pixi.object/type :pixi.object.type/container
        :pixi.container/children
        {:logo
         {:impi/key             "logo"
          :pixi.object/type     :pixi.object.type/sprite
          :pixi.object/position [300 300]
          :pixi.sprite/anchor   [0.5 0.5]
          :pixi.sprite/texture  {:pixi.texture/source "img/clj-melb-logo.png"}}
         :fireworks
         {:impi/key         :fireworks
          :pixi.object/type :pixi.object.type/container
          :pixi.container/children
          (vec (for [i (range 15)]
                 (create-firework! i)))}}}}))

(defn animate [state]
  (swap! state update-fireworks)
  (js/setTimeout #(when (::loop-ms @state) (animate state)) (::loop-ms @state)))

(let [element (.getElementById js/document "app")]
  (impi/mount :example @state element)
  (add-watch state ::mount (fn [_ _ _ s] (impi/mount :example s element))))

(defn ^:export start []
  (when (not @state) (init-stage!))
  (swap! state assoc ::loop-ms 16)
  (animate state))

(defn ^:export stop []
  (swap! state assoc ::loop-ms nil))
