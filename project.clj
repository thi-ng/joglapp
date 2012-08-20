(defproject com.postspectacular/joglapp "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://hg.postspectacular.com/joglui"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.4.0"]
                 [com.jogamp/gluegen-rt "20120617"]
                 [com.jogamp/jogl2 "20120617"]
                 [org.toxiclibs/toxiclibs-clj "0.2.0-SNAPSHOT"]]
  :repositories {"nexus-3rd" {:url "http://50.17.220.26:8089/nexus/content/repositories/thirdparty/" :creds :gpg :snapshots false}
                 "nexus-releases" {:url "http://50.17.220.26:8089/nexus/content/repositories/postspectacular-releases/" :creds :gpg :snapshots false}
                 "nexus-snapshots" {:url "http://50.17.220.26:8089/nexus/content/repositories/postspectacular-snapshots/" :creds :gpg}}
)
