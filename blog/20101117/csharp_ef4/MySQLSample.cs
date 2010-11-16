using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Text;

namespace Fits.Sample
{
    class MySQLSample
    {
        static void Main(string[] args)
        {
            //モデル変更時にテーブルを再作成するための設定
            Database.SetInitializer<BookManager>(new RecreateDatabaseIfModelChanges<BookManager>());

            AddData();
            SelectData();
        }

        //データ追加
        private static void AddData()
        {
            using (var manager = new BookManager())
            {
                var p1 = new Publisher
                {
                    Name = "テスト1",
                    Address = "神奈川県・・・"
                };

                manager.Publishers.Add(p1);
                manager.Publishers.Add(new Publisher
                {
                    Name = "test2",
                    Address = "東京都・・・"
                });

                manager.Books.Add(new Book
                {
                    Title = "Entity Framework CTP4",
                    Publisher = p1
                });

                manager.Books.Add(new Book
                {
                    Title = "MySQL",
                    Publisher = p1
                });

                manager.SaveChanges();
            }
        }

        //データ検索
        private static void SelectData()
        {
            using (var manager = new BookManager())
            {
                //Include で Books の内容をロードするように指定
                //Include が無いと Books プロパティの値が null になる
                var res = from p in manager.Publishers.Include("Books")
                          where p.Books.Count > 0
                          select p;

                res.ToList().ForEach(p =>
                {
                    Console.WriteLine("publisher: {0}", p.Name);
                    p.Books.ToList().ForEach(b => Console.WriteLine("book: {0}", b.Title));
                    Console.WriteLine();
                });
            }
        }
    }

    public class Publisher
    {
        public int PublisherId { get; set; }
        public string Name { get; set; }
        public string Address { get; set; }
        public ICollection<Book> Books { get; set; }
    }

    public class Book
    {
        public int BookId { get; set; }
        public string Title { get; set; }
        public Publisher Publisher { get; set; }
    }

    public class BookManager : DbContext
    {
        public DbSet<Publisher> Publishers { get; set; }
        public DbSet<Book> Books { get; set; }
    }
}
