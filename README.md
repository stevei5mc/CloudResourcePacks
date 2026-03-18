# **CloudResourcePacks 云资源包插件**

## **介绍**
- **本插件可以在一定程度上提供类似于[EaseCation](https://www.easecation.net)服务器的云材质功能**

### **支持功能**
- [x] **为拥有指定权限的玩家提供指定的资源包**

### **注意事项**
1. **使用本插件后需要对资源包进行分配，否则不能将资源包正常的发送给玩家的**
2. **`default_packs.yml`配置的是对全体玩家提供的资源包**
3. **`need_permission_packs.yml`配置的是需要指定权限才能够进行发送给玩家的资源包**

### **使用教程**
1. UUID 从哪找？
  - 打开包的 manifest.json，复制 header 里的 uuid。
  - ❌ 不要复制 modules 里的 UUID。
2. 怎么填？
  - 默认包与权限包均可填写多个包`
  - 默认包 (default_packs.yml)：直接填 UUID 列表。
  - 权限包 (need_permission_packs.yml)：先写权限名，再在下方列表填对应的 UUID。
3. 配置示例
default_packs.yml
```yml
# 默认资源包配置
# 所有玩家都会自动加载这些包
list:
  - "123e4567-e89b-12d3-a456-426614174000"
  - "987fcdeb-51a2-43d1-9a87-426614174111"
```
need_permission_packs.yml
```yml
# 权限资源包配置
# 只有拥有对应权限的玩家才能加载这些包
# 权限名称: [命名空间.权限标识]
vip.pack.1:        # 需要先定义此权限
  - "abcdef12-3456-7890-abcd-ef1234567890"  # VIP专属纹理包
```