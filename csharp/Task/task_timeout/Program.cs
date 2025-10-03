await TimeoutTest();

async Task TimeoutTest()
{
    try
    {
        await Process(1000).WaitAsync(TimeSpan.FromSeconds(2));

        Console.WriteLine("-----");

        await Process(3000).WaitAsync(TimeSpan.FromSeconds(2));
    }
    catch(Exception e)
    {
        Console.WriteLine(e);
    }
}

async Task Process(int workTime)
{
    Console.WriteLine($"start process: {workTime}");

    await Task.Delay(workTime);

    Console.WriteLine($"end process: {workTime}");
}