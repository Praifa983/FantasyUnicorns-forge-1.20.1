"""สร้างโมเดล GeckoLib, แอนิเมชัน และเท็กซ์เจอร์ของยูนิคอร์น (placeholder คุณภาพใช้งานได้ แก้ต่อใน Blockbench ได้)"""
import json, math, os, random
from PIL import Image

RES = "src/main/resources"
MOD = "fantasyunicorns"
W, H = 128, 64

# ---------------- โครงสร้างโมเดล ----------------
# (bone, parent, pivot, [cubes])  cube = (name, origin, size, uv, role)
LEG_X = [-4.5, 1.5]; LEG_Z = [-8.5, 5.5]
bones = []
def bone(name, parent, pivot, cubes=None):
    bones.append(dict(name=name, parent=parent, pivot=pivot, cubes=cubes or []))

bone("root", None, [0, 0, 0])
bone("body", "root", [0, 13, 0], [("body", [-5, 13, -9], [10, 9, 18], [0, 0], "body")])
bone("saddle", "body", [0, 22, 0], [("saddle", [-5.5, 22, -4], [11, 2, 8], [60, 0], "saddle")])
bone("neck", "body", [0, 20, -8], [
    ("neck_a", [-2, 19, -10], [4, 7, 4], [100, 0], "body"),
    ("neck_b", [-2, 25, -12], [4, 6, 4], [100, 12], "body"),
    ("mane_a", [-1, 19, -6], [2, 7, 2], [52, 28], "mane"),
    ("mane_b", [-1, 25, -8], [2, 7, 2], [62, 28], "mane")])
bone("head", "neck", [0, 29, -10], [("head", [-2.5, 25, -19], [5, 6, 9], [0, 28], "head")])
bone("horn", "head", [0, 31, -16], [("horn", [-0.5, 31, -17], [1, 7, 1], [30, 28], "horn")])
bone("ear_l", "head", [1.75, 31, -12], [("ear_l", [1, 31, -12], [2, 3, 1], [36, 28], "ear")])
bone("ear_r", "head", [-1.75, 31, -12], [("ear_r", [-3, 31, -12], [2, 3, 1], [44, 28], "ear")])
bone("tail", "body", [0, 21, 9], [("tail", [-1.5, 11, 9], [3, 11, 3], [72, 28], "mane")])
leg_uv = [[0, 46], [14, 46], [28, 46], [42, 46]]
hoof_uv = [[56, 46], [70, 46], [84, 46], [98, 46]]
legs = [("leg_fr", 0, 0), ("leg_fl", 1, 0), ("leg_br", 0, 1), ("leg_bl", 1, 1)]
for i, (n, xi, zi) in enumerate(legs):
    x, z = LEG_X[xi], LEG_Z[zi]
    bone(n, "body", [x + 1.5, 13, z + 1.5], [
        (n + "_upper", [x, 2, z], [3, 11, 3], leg_uv[i], "body"),
        (n + "_hoof", [x, 0, z], [3, 2, 3], hoof_uv[i], "hoof")])

def build_geo():
    out = []
    for b in bones:
        d = {"name": b["name"], "pivot": b["pivot"]}
        if b["parent"]: d["parent"] = b["parent"]
        if b["cubes"]:
            d["cubes"] = [{"origin": c[1], "size": c[2], "uv": c[3]} for c in b["cubes"]]
        out.append(d)
    return {"format_version": "1.12.0", "minecraft:geometry": [{
        "description": {"identifier": "geometry.unicorn", "texture_width": W, "texture_height": H,
                        "visible_bounds_width": 4, "visible_bounds_height": 4, "visible_bounds_offset": [0, 1, 0]},
        "bones": out}]}

# ---------------- แอนิเมชัน ----------------
def keys(fn, length, n):
    return {f"{length*i/n:.4f}": fn(2*math.pi*i/n) for i in range(n+1)}

def build_anims():
    A = {}
    A["animation.unicorn.idle"] = {"loop": True, "animation_length": 4.0, "bones": {
        "body": {"position": keys(lambda p: [0, 0.25*math.sin(p), 0], 4.0, 8)},
        "neck": {"rotation": keys(lambda p: [2*math.sin(p), 0, 0], 4.0, 8)},
        "tail": {"rotation": keys(lambda p: [0, 8*math.sin(p), 4*math.sin(p)], 4.0, 8)},
        "ear_l": {"rotation": keys(lambda p: [0, 0, 6*max(0, math.sin(2*p))], 4.0, 8)},
        "ear_r": {"rotation": keys(lambda p: [0, 0, -6*max(0, math.sin(2*p+1))], 4.0, 8)},
        "horn": {"scale": keys(lambda p: [1, 1+0.05*math.sin(p), 1], 4.0, 8)}}}
    def gait(name, length, amp, bob):
        s = lambda ph: (lambda p: [amp*math.sin(p+ph), 0, 0])
        A[name] = {"loop": True, "animation_length": length, "bones": {
            "leg_fl": {"rotation": keys(s(0), length, 8)},
            "leg_br": {"rotation": keys(s(0), length, 8)},
            "leg_fr": {"rotation": keys(s(math.pi), length, 8)},
            "leg_bl": {"rotation": keys(s(math.pi), length, 8)},
            "body": {"position": keys(lambda p: [0, bob*abs(math.sin(p)), 0], length, 8),
                     "rotation": keys(lambda p: [bob*2*math.sin(2*p), 0, 0], length, 8)},
            "neck": {"rotation": keys(lambda p: [amp*0.12*math.sin(2*p), 0, 0], length, 8)},
            "tail": {"rotation": keys(lambda p: [amp*0.2, 12*math.sin(p), 0], length, 8)}}}
    gait("animation.unicorn.walk", 1.0, 28, 0.3)
    gait("animation.unicorn.run", 0.6, 50, 0.9)
    A["animation.unicorn.cast"] = {"loop": "hold_on_last_frame" and False, "animation_length": 0.5, "bones": {
        "head": {"rotation": {"0.0": [0, 0, 0], "0.15": [-28, 0, 0], "0.35": [-28, 0, 0], "0.5": [0, 0, 0]}},
        "leg_fl": {"rotation": {"0.0": [0, 0, 0], "0.15": [-35, 0, 0], "0.25": [10, 0, 0], "0.5": [0, 0, 0]}},
        "horn": {"scale": {"0.0": [1, 1, 1], "0.15": [1.5, 1.4, 1.5], "0.35": [1.5, 1.4, 1.5], "0.5": [1, 1, 1]}}}}
    return {"format_version": "1.8.0", "animations": A}

# ---------------- เท็กซ์เจอร์ ----------------
PAL = {  # id: (body, mane, horn, eye)
    "aurelia":  ((245, 245, 250), (255, 222, 130), (255, 215, 90),  (40, 40, 70)),
    "nyx":      ((40, 36, 56),    (100, 45, 150),  (175, 105, 255), (230, 230, 255)),
    "rosalie":  ((255, 192, 216), (255, 120, 175), (255, 240, 170), (70, 30, 60)),
    "glacia":   ((172, 226, 250), (90, 170, 255),  (205, 250, 255), (20, 50, 100)),
    "solara":   ((250, 206, 72),  (255, 140, 30),  (255, 245, 160), (90, 50, 10)),
    "amethyra": ((188, 142, 236), (120, 70, 200),  (240, 200, 255), (50, 20, 80)),
    "lunaris":  ((202, 207, 218), (130, 150, 190), (232, 242, 255), (30, 40, 70)),
    "verdelia": ((112, 216, 152), (40, 150, 90),   (212, 255, 190), (10, 60, 30)),
    "ignis":    ((216, 62, 50),   (255, 140, 30),  (255, 222, 120), (255, 240, 160)),
    "prisma":   ((250, 250, 255), (255, 100, 200), (255, 255, 255), (40, 40, 70)),
    "celestia": ((26, 26, 72),    (130, 105, 255), (255, 255, 205), (255, 255, 255)),
}
BROWN = (125, 78, 46); HOOF = (60, 50, 60)

def rainbow(t):
    import colorsys
    r, g, b = colorsys.hsv_to_rgb(t % 1.0, 0.55, 1.0)
    return (int(r*255), int(g*255), int(b*255))

def shade(c, f): return tuple(max(0, min(255, int(v*f))) for v in c)

def faces(origin, size, uv):
    w, h, d = size; u, v = uv
    return {  # name: (x, y, w, h, brightness)
        "top": (u+d, v, w, d, 1.08), "bottom": (u+d+w, v, w, d, 0.72),
        "east": (u, v+d, d, h, 0.92), "north": (u+d, v+d, w, h, 1.0),
        "west": (u+d+w, v+d, d, h, 0.85), "south": (u+2*d+w, v+d, w, h, 0.9)}

def paint(vid):
    body, mane, horn, eye = PAL[vid]
    img = Image.new("RGBA", (W, H), (0, 0, 0, 0)); glow = Image.new("RGBA", (W, H), (0, 0, 0, 0))
    rnd = random.Random(hash(vid) & 0xffff)
    for b in bones:
        for (cname, origin, size, uv, role) in b["cubes"]:
            for fname, (x, y, fw, fh, br) in faces(origin, size, uv).items():
                for j in range(int(fh)):
                    for i in range(int(fw)):
                        px, py = int(x)+i, int(y)+j
                        n = rnd.uniform(0.96, 1.04)
                        if role == "body" or role == "head": c = body
                        elif role == "ear": c = body
                        elif role == "hoof": c = HOOF
                        elif role == "saddle": c = BROWN
                        elif role == "horn": c = horn if vid != "prisma" else rainbow(j/ max(fh,1))
                        else:  # mane / tail
                            c = mane if vid != "prisma" else rainbow((j + i*2)/10.0)
                            if (j % 3 == 2): c = shade(c, 0.82)
                        if role == "saddle" and (i in (1, int(fw)-2)): c = (200, 170, 70)
                        col = shade(c, br*n)
                        img.putpixel((px, py), col + (255,))
                        if role in ("mane", "horn"):
                            gc = c if role != "horn" else shade(horn, 1.0)
                            glow.putpixel((px, py), shade(gc, 1.05 if role == "mane" else 1.0) + (255,))
    # ดวงตา + จมูก บนหัว (head cube uv [0,28] size [5,6,9])
    hu, hv, hw, hh, hd = 0, 28, 5, 6, 9
    for (ex, ey) in [(hu+hd-3, hv+hd+1), (hu+hd+hw+2, hv+hd+1)]:
        img.putpixel((ex, ey), eye + (255,)); img.putpixel((ex+1, ey), eye + (255,))
        img.putpixel((ex, ey+1), eye + (255,)); img.putpixel((ex+1, ey+1), eye + (255,))
    for nx in (hu+hd+1, hu+hd+hw-2): img.putpixel((nx, hv+hd+hh-2), shade(body, 0.5) + (255,))
    # ลวดลายพิเศษ
    if vid == "celestia":
        for _ in range(60):
            px, py = rnd.randrange(0, 56), rnd.randrange(0, 27)
            if img.getpixel((px, py))[3]: img.putpixel((px, py), (255, 255, 255, 255)); glow.putpixel((px, py), (255, 255, 255, 255))
    if vid == "prisma":
        for _ in range(40):
            px, py = rnd.randrange(0, 56), rnd.randrange(0, 27)
            if img.getpixel((px, py))[3]: img.putpixel((px, py), rainbow(rnd.random()) + (255,))
    if vid == "ignis":
        for _ in range(25):
            px, py = rnd.randrange(0, 56), rnd.randrange(0, 27)
            if img.getpixel((px, py))[3]: img.putpixel((px, py), (255, 170, 60, 255)); glow.putpixel((px, py), (255, 170, 60, 255))
    return img, glow

def item_icons():
    # magic apple
    img = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(16):
        for x in range(16):
            d = math.hypot(x-7.5, y-9.5)
            if d <= 5.8 or (abs(x-7.5) < 6 and abs(y-9.5) < 4.5 and d < 6.5):
                light = 1.15 - 0.05*math.hypot(x-5.5, y-7.5)
                img.putpixel((x, y), tuple(min(255, int(v*light)) for v in (200, 90, 230)) + (255,))
    for x, y in [(7, 3), (8, 3), (8, 4), (7, 4)]: img.putpixel((x, y), (110, 70, 40, 255))
    for x, y in [(9, 3), (10, 2), (11, 2), (10, 3)]: img.putpixel((x, y), (90, 200, 110, 255))
    for x, y in [(5, 7), (4, 8), (11, 12), (12, 5), (3, 12)]: img.putpixel((x, y), (255, 255, 255, 255))
    for x, y in [(5, 8), (6, 7), (12, 12), (11, 5), (3, 11)]: img.putpixel((x, y), (255, 230, 120, 255))
    img.save(f"{RES}/assets/{MOD}/textures/item/magic_apple.png")
    # leaves
    r = random.Random(7); lv = Image.new("RGBA", (16, 16))
    for y in range(16):
        for x in range(16):
            t = r.random()
            c = (255, 175, 215) if t < 0.55 else (240, 135, 190) if t < 0.8 else (255, 232, 246)
            lv.putpixel((x, y), c + (0 if r.random() < 0.07 else 255,))
    lv.save(f"{RES}/assets/{MOD}/textures/block/pastel_leaves.png")
    # flower
    fl = Image.new("RGBA", (16, 16), (0, 0, 0, 0))
    for y in range(8, 16): fl.putpixel((8, y), (70, 170, 100, 255))
    for x, y in [(9, 12), (10, 11), (7, 13), (6, 12)]: fl.putpixel((x, y), (90, 200, 120, 255))
    for y in range(16):
        for x in range(16):
            d = math.hypot(x-8, y-5)
            if d <= 3.6: fl.putpixel((x, y), (200, 130, 255, 255) if d > 1.5 else (255, 245, 130, 255))
    fl.save(f"{RES}/assets/{MOD}/textures/block/glow_flower.png")

def w(path, obj):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f: json.dump(obj, f, indent=2, ensure_ascii=False)

if __name__ == "__main__":
    for d in ["textures/item", "textures/block", "textures/entity/unicorn"]:
        os.makedirs(f"{RES}/assets/{MOD}/{d}", exist_ok=True)
    w(f"{RES}/assets/{MOD}/geo/entity/unicorn.geo.json", build_geo())
    w(f"{RES}/assets/{MOD}/animations/entity/unicorn.animation.json", build_anims())
    for vid in PAL:
        t, g = paint(vid)
        t.save(f"{RES}/assets/{MOD}/textures/entity/unicorn/{vid}.png")
        g.save(f"{RES}/assets/{MOD}/textures/entity/unicorn/{vid}_glowmask.png")
    item_icons()
    print("assets ok")
