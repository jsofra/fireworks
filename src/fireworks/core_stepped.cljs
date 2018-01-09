(ns fireworks.core_stepped
  (:require [impi.core :as impi]))

(enable-console-print!)

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

(defn spark [pos]
  {:impi/key             "spark"
   :pixi.object/type     :pixi.object.type/sprite
   :pixi.object/position pos
   :pixi.sprite/anchor   [0.5 0.5]
   :pixi.sprite/texture  {:pixi.texture/source "img/spark.png"}})

(def stage
  {:impi/key                :stage
   :pixi.object/type        :pixi.object.type/container
   :pixi.container/children [clj-melb-logo
                             (spark [300 150])]})

(def state
  {:pixi/renderer canvas
   :pixi/stage    stage})

(impi/mount :fireworks state (.getElementById js/document "app"))
