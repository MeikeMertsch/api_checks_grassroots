(ns test-automation.your-tv.meta.structures)

(defn generate-right [[available premium airplay fastforward pause rewind]]
  {:available   available
   :premium     premium
   :airplay     airplay
   :fastforward fastforward
   :pause       pause
   :rewind      rewind})

(defn generate-link [id]
  (str "HIDDEN" id ".png"))

(defn generate-airing [type [[showId description channel-name title channel-id id]
                             [startUnixtime streamStartUnixtime stopUnixtime availableToUnixtime]
                             [channel-streamDelay]
                             [logoLight logoDark image]
                             [rigths-live rights-recorded]
                             [channel-rights-viewRecorded]]]
  {:type   type
   :airing {:showId                showId
            :description           description
            :startUnixtime         startUnixtime
            :channel               {:id          channel-id
                                    :name        channel-name
                                    :logoLight   logoLight
                                    :logoDark    logoDark
                                    :streamDelay channel-streamDelay
                                    :rights      {:viewRecorded channel-rights-viewRecorded}}
            :title                 title
            :rights                {:live     rigths-live
                                    :recorded rights-recorded}
            :id                    id
            :availableFromUnixtime startUnixtime
            :streamStartUnixtime   streamStartUnixtime
            :image                 image
            :stopUnixtime          stopUnixtime
            :availableToUnixtime   availableToUnixtime}})

(defn generate-subsection [type [[id title]
                                 airings]]
  {:type       type
   :subsection {:id    id
                :title title
                :items airings}})

(defn generate-fruitbox [type [fruitbox-id fruitbox-image]]
  {:type     type
   :fruitbox {:id    fruitbox-id
              :image fruitbox-image}})

(defn generate-section [[[id title]
                         sectionItems]]
  {:id           id
   :title        title
   :sectionItems sectionItems})

