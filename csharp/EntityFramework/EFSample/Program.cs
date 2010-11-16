using System;
using System.Collections.Generic;
using System.Data.Entity;
using System.Data.Entity.Infrastructure;
using System.Linq;
using System.Text;

namespace EFSample
{
    class Program
    {
        static void Main(string[] args)
        {
            //テーブルを自動で生成するための設定
            Database.SetInitializer<BookManager>(new RecreateDatabaseIfModelChanges<BookManager>());

            using (var manager = new BookManager())
            {
                manager.Publishers.Add(new Publisher
                {
                    Name = "テスト1",
                    Address = "東京都"
                });

                manager.SaveChanges();
            }

            Console.WriteLine("End");
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
        public int PublisherId { get; set; }
        public Publisher Publisher { get; set; }
        public string Isbn { get; set; }

    }

    public class BookManager : DbContext
    {
        public DbSet<Publisher> Publishers { get; set; }
        public DbSet<Book> Books { get; set; }
    }
}
