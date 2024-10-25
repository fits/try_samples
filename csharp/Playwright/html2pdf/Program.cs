using Microsoft.Playwright;

var html = args[0];

using var playwright = await Playwright.CreateAsync();
await using var browser = await playwright.Chromium.LaunchAsync();

var page = await browser.NewPageAsync();
await page.SetContentAsync(html);

var data = await page.PdfAsync();

File.WriteAllBytes("output.pdf", data);
