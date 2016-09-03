package sample

import akka.persistence.journal.{Tagged, WriteEventAdapter}

class SampleTaggingEventAdapter extends WriteEventAdapter {
  override def manifest(event: Any): String = ""

  override def toJournal(event: Any): Any = {
    Tagged(event, Set("sampletag"))
  }
}
