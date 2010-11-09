using System;
using System.Collections.Generic;
using System.Text;

using AmazonTest.com.amazon.webservices;

namespace AmazonTest
{
    class Program
    {
        static void Main(string[] args)
        {
            if (args.Length < 1)
            {
                Console.WriteLine("AmazonTest [keyword]");
                return;
            }

            AWSECommerceService service = new AWSECommerceService();

            ItemSearchRequest req = new ItemSearchRequest();
            req.Keywords = args[0];
            req.SearchIndex = "Books";
            req.ResponseGroup = new string[] {"ItemAttributes"};
            req.ItemPage = "1";

            ItemSearch search = new ItemSearch();
            search.AWSAccessKeyId = "xxx";
            search.Request = new ItemSearchRequest[] { req };

            ItemSearchResponse res = service.ItemSearch(search);

            if (res.Items[0].Request.Errors != null)
            {
                Console.WriteLine("error:{0}", res.Items[0].Request.Errors[0].Message);
            }

            if (res.Items[0].Item != null)
            {
                foreach (Item item in res.Items[0].Item)
                {
                    Console.WriteLine("title:{0}", item.ItemAttributes.Title);
                }
            }
            
        }
    }
}
