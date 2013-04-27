(defproject com.postspectacular/joglapp "0.1.3-SNAPSHOT"
  :description "Basic app skeleton & utilities for rapid prototyping w/ Clojure and JOGL2"
  :url "http://hg.postspectacular.com/joglapp"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [org.clojars.toxi/gluegen-rt "2.0.0-rc11"]
                 [org.clojars.toxi/jogl "2.0.0-rc11"]
                 ;[com.jogamp/gluegen-rt "2.0.0" :classifier "rc10"]
                 ;[com.jogamp/jogl "2.0.0" :classifier "rc10"]
                 [com.postspectacular/toxi2 "0.1.1"]]
  :repositories {"nexus-3rd" {:url "http://50.17.220.26:8089/nexus/content/repositories/thirdparty/" :creds :gpg :snapshots false}
                 "nexus-releases" {:url "http://50.17.220.26:8089/nexus/content/repositories/postspectacular-releases/" :creds :gpg :snapshots false}
                 "nexus-snapshots" {:url "http://50.17.220.26:8089/nexus/content/repositories/postspectacular-snapshots/" :creds :gpg}
                 "local-3rd" {:url "http://localhost:8089/nexus/content/repositories/thirdparty/" :snapshots false}}
)
