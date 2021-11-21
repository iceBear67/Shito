# 上手使用

Shito 服务/程序是完全免费的，但你可以选择[捐助](https://afdian.net/@omgib67) iceBear67，因为服务器开销并不低。

在准备好使用 Shito 前，请先添加您的机器人（或者 Polar ）为好友并且接收消息通知。
> 使用 Polar 即代表您遵守中国大陆内法律，禁止使用 Polar 发送政治敏感，色情，危害公共利益，仇恨等信息。
> 具体的使用者需要承担一切因违背 Polar 条例使用服务而造成的民事或刑事法律责任。

由于 MiraiAdapter 的 bug，从禁止私聊的群里添加 polar 可能会导致信息无法接受。

# 组成

在使用 Shito 之前。我们需要知道 Shito 是如何工作的。

## 模板

Shito 从公开的 API 接受数据，并且使用类 Jinja2 的模板语法进行处理。目前，Shito 以不同的方法支持了这两种数据类型：

- JSON Shito 对 Json 做了特殊的适配，使您可以在模板中使用 [jsonPath](https://github.com/json-path/JsonPath) 获取数据
  `{"a":{"b":"c"}}` -> `{{ a.b }} == "c"`
- Raw Data Shito 考虑到了简单的信息推送服务，因此我们支持使用纯文本数据，这会导致模板中只存在一个预先定义的 `data` 。  
  `testPayload` -> `{{ data }}`

通过强大的模板语法，您可以轻松的构建出接受来自任何 JSON 回调或是其他数据的通知 Bot。 例:

```
[{{ repository.full_name }}]
{% for commit in commits %}
{{ commit.committer.username }} HKT {{ commit.timestamp | date(existingFormat="yyyy-MM-dd'T'HH:mm:ssX", format = "HH:mm:ss") }} 
#{{ commit.id | slice(0,7) }}({{ ref | split("\/") | last }}):
{{ commit.message | replace( {"
": "
- "} ) }} {# 模板语法对换行符的处理缺陷，以至于必须使用一个真正的换行符。 #}
{% endfor %}
```

生产环境输出：  
![image](https://upload.cc/i1/2021/11/21/CQx1ya.png)

在未来，Shito 还会支持主动拉取数据。

[开始学习模板语法](https://pebbletemplates.io/wiki/guide/basic-usage/)

## 路由

Shito 允许用户将信息传播的更广。  
![image.png](https://i.loli.net/2021/11/21/mV3iRHcLWBYnlOE.png)

除了添加更多发送目标的，Shito 还可以利用 PolarCore 的跨平台特性将信息发送到所有支持的平台上。

# 使用

鉴于开发时间紧的缘故，目前所有提示信息都是英文的，但并不难读懂。    
所有以 `[ ]` 包括的参数均为可选，所有以 `< >` 包括的参数均为**必须**

## 获得帮助

使用 `!p shito` 就可以得到命令菜单。

![image.png](https://i.loli.net/2021/11/21/vhnGxjl3BPakOKR.png)

## 新建模板

使用 `!p shito create <模板名，只支持英文数字大小写和下划线> [预设名]`

在不指定预设名的情况下，Shito 将会在私聊请求您发送模板内容，发送后即可生成 API 信息。

![image.png](https://i.loli.net/2021/11/21/bMUI4fghZ2oX7Kd.png)

使用 `!p shito remove <模板名>` 删除，或者考虑使用 `!p shito enable/disable <模板名>` 来临时启用/禁用    
使用 `!p shito edit <模板名>` 对模板内容进行修改。

## 设置路由

在完成模板的配置后即可开始设置路由。

发送 `!p shito route <模板名>` 即可将当前`群/私信/...` 添加到路由列表里。     
使用 `!p shito status <模板名>` 即可查询路由列表。  
使用 `!p shito delroute <模板名> <索引>` 即可删除路由，索引即为路由列表里路由的位置，从第一个(0)开始。

## 使用预设

出于方便考虑，Shito 一直在收集一些通用的预设，在创建模板时使用预设即可省去配置模板内容的过程。      
![image.png](https://i.loli.net/2021/11/21/K8pS3CDEOmftZHX.png)
使用 `!p shito preset` 即可查看列表。

> 如果您想给 Polar 提供更多模板建议，请在[此处](https://github.com/project-polar/bot/issues)留言。

# v1 API

Shito 开放的简易 Web API。  
目前支持两种上报方式：`HTTP 1.1 POST`，`HTTP 1.1 GET`  
每 60 秒至多发送 20 条。

## Post API

示例 url: `https://bot.sfclub.cc/shito/api/v1/push/testPost/2c8246a8-ac7b-4c6d-80ce-e59e5cc07750`  
注意：这个 URL 最尾部是用户的 secret，请保护好不要泄露。

往示例 URL Post 数据即可。若要使 Shito 启用 jsonpath 支持，需要满足两个条件：

1. `Content-Type: application/json`
2. 数据必须是 Json，长度最长 15000

以上任意一点不满足 Shito 均会将数据作为 `data` 处理（即模板编译环境中只会存在一个 `data` 字段），Shito 会在出现错误的时候直接将错误信息汇报到发送点里。

## Get API

示例 url: `https://bot.sfclub.cc/shito/api/v1/pushGet/testPost/2c8246a8-ac7b-4c6d-80ce-e59e5cc07750/%DATA%`  
注意：这个 URL 倒数第二个是用户的 secret，请保护好不要泄露。

对于数据如何启用 jsonpath 支持，请看上一条，长度最长 200。

数据通过 URL 发送到服务端，但是数据需要满足一个要求: `必须经过URLSafe的Base64编码`    
在 Java 中也很容易实现：

```java
import java.util.Base64;

class Example {
  {
    String payload = Base64.getUrlEncoder().encodeToString("YourData".getBytes());
  }
}
```

其他语言的使用者可以参考 RFC 4648 和 RFC 2045。
