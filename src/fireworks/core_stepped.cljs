(ns fireworks.core_stepped
  (:require [impi.core :as impi]))

(enable-console-print!)

(def canvas-size 600)

(def canvas
  {:pixi.renderer/size             [canvas-size canvas-size]
   :pixi.renderer/background-color 0x0a1c5e
   :pixi.renderer/transparent?     false})

(def stage
  {:impi/key                :stage
   :pixi.object/type        :pixi.object.type/container
   :pixi.container/children []})

(def state
  {:pixi/renderer canvas
   :pixi/stage    stage})
