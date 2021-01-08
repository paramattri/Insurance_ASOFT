package com.manipal.insurance.controller


import com.manipal.insurance.dao.Dao
import com.manipal.insurance.service.Service
import org.bson.Document
import org.json.JSONException
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class Controller {
    @Autowired
    var mongoTemplate: MongoTemplate? = null

    @Autowired
    var service: Service? = null
    var dao: Dao? = null

    @PostMapping("/config")
    @Throws(JSONException::class)
    fun addConfig(@RequestBody data: String?): ResponseEntity<String?> {
        dao = mongoTemplate?.let { Dao(it) }
        val jsonData = JSONObject(data)
        if(jsonData.has("partners")){
            for(i in 0 until jsonData.getJSONArray("partners").length()){
                dao?.insert("partners",Document.parse(jsonData.getJSONArray("partners").getJSONObject(i).toString()))
            }
            jsonData.remove("partners")
        }
        println(jsonData.toString())
        val doc = Document.parse(data)
        dao?.insert("formConfig", doc)
        val headers = HttpHeaders()
        headers.add("Response-from", "ToDoController")
        return ResponseEntity<String?>(jsonData.toString(), headers, HttpStatus.OK)
    }

    @GetMapping("/categories")
    fun retrieveCategory(): List<Document>? {
        return service?.findUniqueCategory()
    }

    @GetMapping("/config/category/{category}/product/{product}")
    fun retrieveConfig(@PathVariable category: String, @PathVariable product: String): List<Document>? {
        return service?.findFormConfig(category, product)
    }

    @PostMapping("/response")
    @Throws(Exception::class)
    fun getData(@RequestBody data: String):List<Document> ?{
    return service?.apiRequests(data)
    }
}