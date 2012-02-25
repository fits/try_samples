package fits.sample.resource;

import java.util.*;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import fits.sample.model.*;

@Path("book")
public class BookResource {
	@GET
	@Produces("application/json")
	public Collection<Book> findBookList() {
		ArrayList<Book> result = new ArrayList<>();

		Book b1 = new Book("1", "テスト1");
		b1.comments.add(new Comment("・・・・"));
		b1.comments.add(new Comment("サンプル"));
		result.add(b1);

		Book b2 = new Book("2", "テスト2");
		result.add(b2);

		return result;
	}

	@POST
	@Consumes("application/json")
	public Response addBook(Book book) {

		System.out.printf("id: %s, title: %s\n", book.id, book.title);
		for (Comment comment : book.comments) {
			System.out.printf("    content : %s\n", comment.content);
		}

		return Response.ok(true).build();
	}
}
