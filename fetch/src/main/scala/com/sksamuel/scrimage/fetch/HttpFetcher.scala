package com.sksamuel.scrimage.fetch

import com.sksamuel.scruffy.client.ScruffyClient
import com.sksamuel.scrimage.Image
import scala.concurrent.{ExecutionContext, Future}

/** @author Stephen Samuel */
trait Fetcher
class HttpFetcher(client: ScruffyClient) extends Fetcher {

  def fetch(url: String)(implicit executionContext: ExecutionContext): Future[Image] = {
    client.prepareGet(url).execute().map(resp => {
      Image(resp.bodyAsBytes)
    })
  }

  def fetch(urls: String*)(implicit executionContext: ExecutionContext): Seq[Future[Image]] = urls.map(fetch)
}

object HttpFetcher {
  def apply: HttpFetcher = new HttpFetcher(ScruffyClient())
}