## `TDS`浏览器设计后端设计文档

### 目标

此文档是针对浏览器的后端接口进行设计，目的是为了确定后端的实现逻辑。

## 修正历史

| 动作 | 人员      | 版本  | 日期          | 备注 |
| ---- | --------- | ----- | ------------- | ---- |
| 新建 | `liuyong` | 1.0.0 | 2020年11月5日 |      |

### 基本组件
* 语言：`java` 8
* 框架： `Springcloud`
* 依赖：`maven`
* 数据库： `postgressql`
* 容器 docker

### 设计表清单
表英文名|中文名
-|-
header|区块头表
transaction|事务表
contract|合约表
sync_height|同步高度表

### 表列清单

#### 区块头表

代码|名称|数据类型|主键|备注
-|-|-|-|-
block_hash|区块hash|string|是|
version|版本|string||PoA=1634693120, PoW=7368567, PoS=7368563
hash_prev|父区块的哈希值|string||
tx_root|事务的梅克尔根|string||
state_root|状态树树根|string||
block_height|区块高度|long||
payload|负载|string||
enter_block_at|进去区块的时间|date||
created_at|记录的创建时间|date||
updated_at|记录的修改时间|date||

#### 事务表

代码|名称|数据类型|主键|备注
-|-|:-|-|-
tx_hash|事务hash|string|是|
tx_block_height|事务的区块高度|long||
tx_block_hash|区块hash|string||
tx_version|事务版本|string||
tx_type|事务类型|string||0=COINBASE, 1=转账, 2=合约部署, 3=合约调用
tx_nonce|nonce|long||
tx_from|from|string||
tx_to|to|string||to也是合约地址
tx_signature|签名|string||
tx_payload|负载|string||
tx_gas_price|gas单价|long||
amount|金额|long||
position|位置|int||
tx_created_at|事务创建时间|date||
created_at|记录的创建时间|date||
updated_at|记录的修改时间|date||

#### 合约表

| 代码    | 名称       | 数据类型 | 主键 | 备注 |
| ------- | ---------- | -------- | ---- | ---- |
| address | 合约地址   | string   | 是   |      |
| tx_hash | 事务hash   | string   |      |      |
| binary  | 合约字节码 | byte[]   |      |      |
| abi     | abi        | byte[]   |      |      |
| height  | 区块高度   | long     |      |      |
| enter_block_at  | 区块时间   | long     |      |      |
| from  | 事务的from   | string     |      |      |
| to  | 事务的to   | string     |      |      |
|created_at|记录的创建时间|date||
|updated_at|记录的修改时间|date||

#### 同步高度表
| 代码    | 名称       | 数据类型 | 主键 | 备注 |
| ------- | ---------- | -------- | ---- | ---- |
| id | 主键   | int   | 是   |      |
| sync_block_height | 同步区块的高度   | int   |    |      |
| sync_contract_height | 同步合约的高度   | int   |    |      |

### 功能

#### 同步数据

* 同步区块

  同步区块，写入到区块头和事务表中即可

* 同步合约

  同步到合约表即可

#### 后端相应接口

* 分页查询区块列表的接口

* 根据事务hash查询事务

* 根据区块高度和区块hash查询区块

* 根据from或to查询交易记录

  根据from和to查询transaction表的记录

* 合约列表接口 合约地址、事务hash，区块高度、区块时间、from、to、amount

* 验证合约接口，与节点验证合约address和合约源码binary，通过后保存到合约表

* 通过合约地址查询合约交易事务

  查询transaction表中type=3, to是合约地址的事务列表

* 根据abi查询binary

#### 需要节点提供的接口

* 根据区块高度区间查询区块列表

* 查询最高的区块高度

* 根据高度，获取这个区块下的合约列表，至少包含智能合约源代码、合约ABI

* 显示共识机制、区块高度。出块平均时间、总事务数

#### 后端服务

#####  同步服务

* 同步区块
* 同步合约

#####  web服务

* `web`api的服务
* 相应的service

##### 注册中心
* eureka-server

##### 流程图

![flow](.\flow.png)