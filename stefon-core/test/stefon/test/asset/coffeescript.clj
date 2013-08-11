(ns stefon.test.asset.coffeescript
  (:require [stefon.test.helpers :as h]
            [stefon.asset.coffeescript :as cs]
            [clojure.java.io :as io])
  (:use clojure.test))

(deftest test-preprocess-coffeescript
  (testing "we have a chance to succeed"
    (is (.exists (io/file "test/fixtures/assets/javascripts/test.js.coffee"))))
  (testing "basic coffee file"
    (is (= "(function() {\n\n  (function(param) {\n    return alert(\"x\");\n  });\n\n}).call(this);\n"
           (cs/preprocess-coffeescript
            (io/file "test/fixtures/assets/javascripts/test.js.coffee")))))
  (testing "syntax error"
    (try
      (cs/preprocess-coffeescript
       (io/file "test/fixtures/assets/javascripts/bad.js.coffee"))
      (is false) ; must throw
      (catch Exception e
        (is (h/has-text? (.toString e) "on line 2"))
        (is (h/has-text? (.toString e) "unmatched ]"))))))