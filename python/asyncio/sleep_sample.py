
import asyncio

async def main():
    print('start ...')
    
    await asyncio.sleep(3)
    
    print('end')

asyncio.run(main())
