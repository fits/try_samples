
import grails.converters.*

class ProductController {
    
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ productList: Product.list( params ) ]
    }

    def show = {
        [ product : Product.get( params.id ) ]
    }

    def delete = {
        def product = Product.get( params.id )
        if(product) {
            product.delete()
            flash.message = "Product ${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "Product not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def product = Product.get( params.id )

        if(!product) {
            flash.message = "Product not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ product : product ]
        }
    }

    def update = {
        def product = Product.get( params.id )
        if(product) {
            product.properties = params
            if(!product.hasErrors() && product.save()) {
                flash.message = "Product ${params.id} updated"
                redirect(action:show,id:product.id)
            }
            else {
                render(view:'edit',model:[product:product])
            }
        }
        else {
            flash.message = "Product not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def product = new Product()
        product.properties = params
        return ['product':product]
    }

    def save = {
        def product = new Product(params)
        if(!product.hasErrors() && product.save()) {
            flash.message = "Product ${product.id} created"
            redirect(action:show,id:product.id)
        }
        else {
            render(view:'create',model:[product:product])
        }
    }

    def search = {
    }

    def searchResult = {
        def results = Product.list()
        def jsonString = ""

        results.each {
            jsonString += "['${it.name}', '${it.fileName}'],"
        }

        render "[${jsonString}]"
    }

    def ajaxtest = {
    }

    def find = {
    //ドキュメントによると以下で自動マーシャリングされるはずだが
    //GroovyCastException が発生
    //
    //  render Product.list() as JSON

        render new JSON(Product.list())
    }

}