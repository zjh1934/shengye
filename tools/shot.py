#!/usr/bin/env python3
"""把设计稿渲染成真机尺寸的截图，供人工/视觉验收。

为什么要有这个：不看到真机像素，就不知道做出来什么鸟样。
所有页面预览都要走这里，别用眼睛猜 CSS 写对没写对。

用法：
    python3 tools/shot.py preview/real/home.html preview/real/home.png
    python3 tools/shot.py a.html b.png --w 360 --h 800 --scale 3   # 小屏/高分屏
    python3 tools/shot.py a.html b.png --sel "#app"                # 只截某元素

默认视口 412x915 @2x（Pixel 7），这是 Android 上最有代表性的尺寸。
"""
import argparse
import asyncio
import pathlib
import sys

from playwright.async_api import async_playwright


async def shoot(src: str, out: str, w: int, h: int, scale: float, sel: str) -> int:
    url = pathlib.Path(src).resolve().as_uri()
    async with async_playwright() as p:
        browser = await p.chromium.launch(args=["--no-sandbox"])
        page = await browser.new_page(
            viewport={"width": w, "height": h},
            device_scale_factor=scale,
        )
        await page.goto(url)
        # 等字体与 backdrop-filter 落定，否则会截到半透明的中间态
        await page.wait_for_timeout(400)
        el = await page.query_selector(sel)
        if el is not None:
            await el.screenshot(path=out)
        else:
            await page.screenshot(path=out, full_page=True)
        await browser.close()
    return 0


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("src")
    ap.add_argument("out")
    ap.add_argument("--w", type=int, default=412)
    ap.add_argument("--h", type=int, default=915)
    ap.add_argument("--scale", type=float, default=2)
    ap.add_argument("--sel", default=".screen")
    a = ap.parse_args()
    try:
        return asyncio.run(shoot(a.src, a.out, a.w, a.h, a.scale, a.sel))
    except Exception as e:  # noqa: BLE001
        print(f"shot failed: {e}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
