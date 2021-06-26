# WorldSliceConverter

### ワールドスライスコンバーター

![地形イメージ](https://cdn.discordapp.com/attachments/797220314983301150/858463382402498580/unknown.png)

### 使い方

1. まずマイクラでワールドを十分に広げてください。
2. jarをworld/region/の中にある.mcaファイルと同じ位置に置きます。
    ```
    world
     |- playerdata
     |- region
     |   |- r.0.0.mca
     |   |- r.0.1.mca
     |   \- flatworld-X.X-SNAPSHOT.jar ← ここに配置
     |- data
     |- datapack
     |- serverconfig
     |- .....
     \- stats
    ```
3. jarをダブルクリックで起動する。(cmd経由で起動すると進捗が見れます。)
4. マイクラでワールドを読み込んでください。

#### 高さ変更方法
3. cmdで引数に高さを入力します。  
   `java -jar flatworld-X.X-SNAPSHOT.jar <高さ>`