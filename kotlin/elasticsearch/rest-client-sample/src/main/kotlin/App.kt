
import org.apache.http.HttpHost
import org.elasticsearch.action.index.IndexRequest
import org.elasticsearch.action.search.SearchRequest
import org.elasticsearch.client.RequestOptions
import org.elasticsearch.client.RestClient
import org.elasticsearch.client.RestHighLevelClient
import org.elasticsearch.client.indices.GetIndexRequest
import org.elasticsearch.search.builder.SearchSourceBuilder
import org.elasticsearch.search.sort.SortOrder

fun main() {
    val existsIndex = { client: RestHighLevelClient, indexName: String ->
        client.indices().exists(GetIndexRequest(indexName), RequestOptions.DEFAULT)
    }

    val searchMaxId = { client: RestHighLevelClient, indexName: String ->
        val src = SearchSourceBuilder()
                .storedField("_id")
                .sort("_id", SortOrder.DESC)
                .size(1)

        val req = SearchRequest().indices(indexName).source(src)

        val res = client.search(req, RequestOptions.DEFAULT)

        res.hits.hits.firstOrNull()?.id?.toIntOrNull() ?: 0
    }

    val maxId = { client: RestHighLevelClient, indexName: String ->
        if (existsIndex(client, indexName)) searchMaxId(client, indexName)
        else 0
    }

    RestHighLevelClient(RestClient.builder(HttpHost("localhost", 9200))).use { client ->
        val indexName = "items"

        val nextId = maxId(client, indexName) + 1
        val nextIdStr = "%09d".format(nextId)

        println(nextIdStr)

        val req = IndexRequest(indexName)
                .id(nextIdStr)
                .source("name", "item-${nextId}", "value", nextId * 100)

        val res = client.index(req, RequestOptions.DEFAULT)

        println(res)
    }
}
