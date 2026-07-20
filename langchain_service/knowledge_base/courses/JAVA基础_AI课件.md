# 课程名称：Java 基础

## 一、课程概述

### 1. 课程简介
Java 基础是计算机相关专业的核心入门课程，旨在帮助学生建立面向对象编程的基本思想，掌握 Java 语言的核心语法与常用类库。课程从零开始，逐步引导学生理解跨平台原理、基本数据类型、流程控制、数组、类与对象、继承多态、异常处理及常用工具类，为后续的 Java Web 开发、Android 开发以及企业级应用打下坚实基础。

### 2. 教学目标
- **知识目标**：理解 Java 平台架构与运行机制；掌握 Java 基本语法规则；熟练运用面向对象三大特性进行程序设计；掌握常用类库（字符串、集合、包装类等）；具备基本的调试与排错能力。
- **能力目标**：能够独立搭建开发环境，使用 IDE 编写、编译、运行 Java 程序；能读懂 UML 类图并转化为代码；能设计简单的类层次结构；能采用异常处理机制编写健壮的程序。
- **素养目标**：培养良好的编码规范（命名、缩进、注释）；形成面向对象的抽象思维；锻炼查阅 API 文档的自学能力。

### 3. 适用对象
- 高职高专 / 应用型本科计算机、软件技术、大数据等专业大一或大二学生。
- 没有编程基础或仅接触过 C 语言的初学者。

### 4. 参考学时
- 理论教学：36 学时
- 上机实践：36 学时
- 总计：72 学时（可根据实际情况调整为 48-64 学时，适当精简 I/O 和常用类库部分）

---

## 二、知识体系

```text
Java 基础
├── 第1章 Java 语言入门
│   ├── 1.1 Java 发展历史与版本
│   ├── 1.2 Java 平台体系（JVM、JRE、JDK）
│   ├── 1.3 跨平台原理与字节码
│   └── 1.4 第一个 Java 程序：HelloWorld
├── 第2章 开发环境搭建
│   ├── 2.1 JDK 安装与环境变量配置
│   ├── 2.2 常用 IDE（IntelliJ IDEA / Eclipse）
│   └── 2.3 程序编写、编译与运行流程
├── 第3章 Java 基本语法
│   ├── 3.1 标识符、关键字与命名规范
│   ├── 3.2 变量与数据类型（基本类型、引用类型）
│   ├── 3.3 类型转换（自动/强制）
│   ├── 3.4 运算符与表达式
│   ├── 3.5 流程控制（if-else、switch、循环）
│   └── 3.6 数组（一维/二维、遍历、常见异常）
├── 第4章 面向对象编程（基础）
│   ├── 4.1 面向对象思想与三大特征
│   ├── 4.2 类与对象的关系
│   ├── 4.3 属性（成员变量）与方法
│   ├── 4.4 构造方法与 this 关键字
│   ├── 4.5 封装与访问修饰符
│   └── 4.6 static 关键字与代码块
├── 第5章 面向对象编程（进阶）
│   ├── 5.1 继承（extends）与 super
│   ├── 5.2 方法重写（Override）与重载（Overload）
│   ├── 5.3 多态（向上转型、向下转型、instanceof）
│   ├── 5.4 抽象类与 final 关键字
│   └── 5.5 接口（interface）与默认方法
├── 第6章 常用类库与工具类
│   ├── 6.1 Object 类（equals、toString、hashCode）
│   ├── 6.2 包装类与自动装箱/拆箱
│   ├── 6.3 String、StringBuilder、StringBuffer
│   ├── 6.4 基础集合框架（ArrayList、LinkedList、HashMap）
│   └── 6.5 日期时间类（LocalDate、LocalTime、LocalDateTime）
├── 第7章 异常处理
│   ├── 7.1 异常体系结构（Throwable、Error、Exception）
│   ├── 7.2 捕获异常（try-catch-finally）
│   ├── 7.3 抛出异常（throw、throws）
│   └── 7.4 自定义异常
└── 第8章 输入输出（I/O）与文件操作
    ├── 8.1 File 类基本操作
    ├── 8.2 字节流与字符流
    ├── 8.3 缓冲流与转换流
    └── 8.4 对象序列化
```

---

## 三、详细教学内容

### 第1章 Java 语言入门

#### 1.1 Java 发展历史与版本
- Java 由 Sun 公司（后被 Oracle 收购）的 James Gosling 团队于 1995 年发布。
- 命名源于一种咖啡，原设计用于嵌入式设备，后因互联网兴起而转向 Web 开发。
- 重要版本节点：Java SE 5（泛型、注解）、Java SE 8（Lambda、Stream、新日期 API）、Java SE 11（长期支持版本）、Java SE 17（当前主流 LTS）。
- **重点**：理解 LTS 版本的概念，目前企业常用 Java 8 和 Java 17。

#### 1.2 Java 平台体系
- **JVM（Java Virtual Machine）**：运行字节码的虚拟机，屏蔽底层硬件差异。
- **JRE（Java Runtime Environment）**：JVM + 核心类库，用于运行 Java 程序。
- **JDK（Java Development Kit）**：JRE + 开发工具（如 javac、java），用于开发和运行。
- 三者关系：JDK 包含 JRE，JRE 包含 JVM。

#### 1.3 跨平台原理
- Java 源文件（.java）经编译器编译成字节码文件（.class）。
- 字节码在任何安装了对应版本 JVM 的操作系统上都能执行，即“一次编译，到处运行”。
- **难点**：字节码与机器码的区别；JVM 需要针对不同平台进行适配，但字节码是通用的。

#### 1.4 第一个 Java 程序
```java
public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("Hello, Java!");
    }
}
```
- 文件名必须与 public 类名一致（HelloWorld.java）。
- main 方法是程序的入口，格式固定。
- `System.out.println` 为控制台输出。

---

### 第2章 开发环境搭建（实践为主）

#### 2.1 JDK 安装与配置
- 从 Oracle 官网或 OpenJDK 下载安装包。
- 配置环境变量：`JAVA_HOME`（指向 JDK 安装目录），`PATH` 中添加 `%JAVA_HOME%\bin`。
- 验证：命令行输入 `java -version` 和 `javac -version`。

#### 2.2 集成开发环境（IDE）
- **IntelliJ IDEA**（社区版免费）：现阶段最主流的 Java IDE。
- **Eclipse**：老牌开源 IDE，仍有大量教学使用。
- IDE 基本操作：新建工程、创建类、运行、调试、查看报错。

#### 2.3 编译与运行
- 命令行：`javac HelloWorld.java` 生成 `HelloWorld.class`；`java HelloWorld` 执行。
- IDE 一键编译运行，底层仍调用 javac 和 java。

---

### 第3章 Java 基本语法

#### 3.1 标识符与关键字
- 标识符：类名、变量名、方法名等，由字母、数字、下划线、$ 组成，不能以数字开头。
- **命名规范**（重点）：类名首字母大写（PascalCase），变量和方法首字母小写（camelCase），常量全大写加下划线。这是企业强制要求。

#### 3.2 变量与数据类型
- 变量必须先声明再使用。
- **基本数据类型（8种）**：
  - 整型：byte(1)、short(2)、int(4，默认)、long(8，加L)
  - 浮点型：float(4，加F)、double(8，默认)
  - 字符型：char(2，Unicode编码)
  - 布尔型：boolean（仅 true/false）
- **引用数据类型**：类、接口、数组、String 等。
- **重点**：默认值（成员变量有默认值，局部变量无）、精度丢失问题（浮点数运算）。

#### 3.3 类型转换
- 自动类型转换：小范围类型自动提升为大范围（byte→short→int→long→float→double）。
- 强制类型转换：`int a = (int)3.14;` 可能丢失精度。
- **易错点**：byte、short、char 运算时先自动转换为 int。

#### 3.4 运算符
- 算术：`+ - * / % ++ --`
- 关系：`== != > < >= <=`
- 逻辑：`&& || !`（短路特性是**重点**）
- 赋值：`= += -= *= /= %=`
- 三元：`条件 ? 表达式1 : 表达式2`

#### 3.5 流程控制
- 判断：if、if-else、if-else if-else；switch（支持 String，case 穿透现象）
- 循环：for、while、do-while
- 跳转：break（结束循环）、continue（结束本次循环）
- **经典案例**：打印九九乘法表、判断闰年、水仙花数。

#### 3.6 数组
- 定义：`int[] arr = new int[5];` 或 `int[] arr = {1,2,3};`
- 数组长度固定，通过 `length` 属性获取。
- 遍历：普通 for 循环、增强 for 循环（`for(int num : arr)`）
- 常见异常：
  - `ArrayIndexOutOfBoundsException`（下标越界）
  - `NullPointerException`（数组引用为 null）
- 二维数组：`int[][] matrix = new int[3][4];`

---

### 第4章 面向对象编程（基础）

#### 4.1 面向对象思想
- 三大特征：封装、继承、多态。
- 类是对一类事物的抽象描述，对象是类的具体实例。
- 优点：可复用、易扩展、易维护。

#### 4.2 类与对象
```java
public class Student {
    String name;   // 属性
    int age;

    public void study() {  // 方法
        System.out.println(name + " is studying");
    }
}
// 创建对象：Student stu = new Student();
```
- new 关键字在堆中分配内存。

#### 4.3 成员变量与方法
- 成员变量定义在类中方法外，有默认值。
- 方法签名：`修饰符 返回值类型 方法名(参数列表) { 方法体 }`
- 参数传递：基本类型传值，引用类型传地址（**难点**：理解形参改变对实参的影响）。

#### 4.4 构造方法与 this
- 构造方法与类名相同，无返回值，用于创建对象时初始化属性。
- 一旦自定义构造方法，系统不再提供无参构造（**重点**）。
- this 关键字：
  - 区分成员变量和局部变量；
  - this() 调用本类其他构造方法（必须在首行）。

#### 4.5 封装与访问修饰符
- 封装：隐藏内部实现，暴露可控接口。将属性用 `private` 修饰，提供 `getter`/`setter`。
- 访问权限修饰符（从大到小）：
  - `public`：任何地方可访问
  - `protected`：同包、子类
  - `default`（不写）：同包
  - `private`：同类

#### 4.6 static 关键字
- 静态变量（类变量）：属于类，所有对象共享，用类名调用。
- 静态方法：只能访问静态成员，不能使用 this。
- 静态代码块：类加载时执行一次，常用于初始化静态资源。
- **执行顺序**（笔试题经典）：父类静态块→子类静态块→父类构造块→父类构造方法→子类构造块→子类构造方法。

---

### 第5章 面向对象编程（进阶）

#### 5.1 继承
- 使用 `extends` 关键字，Java 只支持单继承。
- 子类拥有父类所有非私有的成员。
- super：访问父类构造方法 `super(参数)`（首行），调用父类普通方法 `super.method()`。
- **重点**：所有类默认继承 Object。

#### 5.2 方法重写与重载
- **重载（Overload）**：同类中方法名相同，参数列表不同（类型、个数、顺序），与返回值无关。
- **重写（Override）**：子类重写父类方法，方法签名完全一致，访问权限不能更低，抛出异常不能更宽。
- `@Override` 注解标记重写，可让编译器检查。

#### 5.3 多态
- 定义：父类引用指向子类对象。`Animal a = new Cat();`
- 编译看左边（父类），运行看右边（子类）。
- 向上转型（自动）、向下转型（强制，需判断 `instanceof`）。
- **多态的意义**：提高代码扩展性，方法参数使用父类型，可接收所有子类对象。

#### 5.4 抽象类与 final
- 抽象类（`abstract`）：不能实例化，可以有抽象方法（无方法体）和普通方法。抽象方法必须被子类重写，除非子类也是抽象类。
- final：修饰变量→常量；修饰方法→不可重写；修饰类→不可继承。

#### 5.5 接口
- 使用 `interface` 定义，早期只能有抽象方法和常量，Java 8 后可定义 `default` 和 `static` 方法。
- 类通过 `implements` 实现接口，可实现多个接口，弥补单继承不足。
- 接口不能实例化，可以声明引用类型接收实现类对象（接口多态）。
- **常用场景**：定义功能规范，如 `Comparable`、`Runnable`。

---

### 第6章 常用类库与工具类

#### 6.1 Object 类
- 所有类的根类。
- `toString()`：返回类名@哈希码，建议重写。
- `equals()`：默认比较地址值，需重写比较内容（**重点**：String 类已重写）。
- `hashCode()`：返回哈希码，重写 equals 时必须同时重写 hashCode，保证相等对象哈希码一致。

#### 6.2 包装类
- 基本类型对应的包装类：Integer、Double、Boolean 等。
- 自动装箱：`Integer i = 100;` 编译后为 `Integer.valueOf(100)`。
- 自动拆箱：`int a = i;` 编译后为 `i.intValue()`。
- 缓存池：Integer 缓存 -128~127，超出后创建新对象，`==` 比较需注意。

#### 6.3 字符串类
- String：不可变字符序列，底层 final char 数组。每次拼接产生新对象。
- StringBuilder：可变字符序列，线程不安全，效率高，单线程首选。
- StringBuffer：可变，线程安全（synchronized），效率较低。
- **常用方法**：`length()`、`charAt()`、`equals()`、`substring()`、`indexOf()`、`split()`。

#### 6.4 基础集合框架
- Collection 接口（单列集合）：
  - List（有序可重复）：ArrayList（数组实现，查询快）、LinkedList（双向链表，增删快）。
  - Set（无序不可重复）：HashSet（哈希表）、TreeSet（红黑树排序）。
- Map 接口（双列）：
  - HashMap（数组+链表+红黑树，键不可重复，值可重复）。
- **遍历方式**：Iterator、增强 for、forEach+Lambda。

#### 6.5 新日期时间 API（Java 8）
- `LocalDate`：日期（年、月、日）
- `LocalTime`：时间（时、分、秒）
- `LocalDateTime`：日期时间组合
- `DateTimeFormatter`：格式化与解析。
- **优点**：线程安全、不可变、API 清晰，逐步取代 Date 和 Calendar。

---

### 第7章 异常处理

#### 7.1 异常体系
- Throwable 分成 Error（严重问题，程序无力处理，如 OutOfMemoryError）和 Exception。
- Exception 分编译时异常（受检异常，如 IOException、SQLException）和运行时异常（非受检，如 NullPointerException、ArithmeticException、IndexOutOfBoundsException）。

#### 7.2 捕获异常
```java
try {
    // 可能出错的代码
} catch (异常类型1 e1) {
    // 处理方式
} catch (异常类型2 e2) {
    // 处理
} finally {
    // 无论是否异常都会执行，常用于释放资源
}
```
- 多 catch 时子类异常在前。
- **重点**：finally 中不要写 return，会覆盖 try 中的返回值。

#### 7.3 抛出异常
- `throw`：在方法体内手动抛出异常对象。
- `throws`：在方法声明中，表示方法可能抛出的异常类型，抛给调用者处理。

#### 7.4 自定义异常
- 继承 Exception 或 RuntimeException。
- 提供构造方法，调用 super 传递异常信息。

---

### 第8章 输入输出与文件操作

#### 8.1 File 类
- 文件和目录路径名的抽象表示。
- 常用方法：`createNewFile()`、`mkdir()`、`mkdirs()`、`exists()`、`delete()`、`listFiles()`。
- 递归遍历文件夹是**经典练习**。

#### 8.2 字节流与字符流
- 字节流（以字节为单位）：`InputStream`/`OutputStream`；文件流：`FileInputStream`/`FileOutputStream`。
- 字符流（以字符为单位，处理文本）：`Reader`/`Writer`；文件流：`FileReader`/`FileWriter`。

#### 8.3 缓冲流与转换流
- 缓冲流提升效率：`BufferedInputStream`、`BufferedReader`、`BufferedWriter`。
- 转换流（字节→字符）：`InputStreamReader`、`OutputStreamWriter`，解决乱码问题。

#### 8.4 对象序列化
- 将对象转换为字节序列保存或传输。
- 类需实现 `Serializable` 接口。
- `ObjectOutputStream` 写对象，`ObjectInputStream` 读对象。
- `transient` 修饰的属性不参与序列化。

---

## 四、课后习题

1. **简答题**  
   请描述 Java 中“一次编译，到处运行”的实现原理，并说明 JVM、JRE、JDK 的区别与联系。

2. **代码分析题**  
   分析以下程序，指出输出结果并解释为什么。
   ```java
   public class Test {
       public static void main(String[] args) {
           Integer a = 100;
           Integer b = 100;
           Integer c = 200;
           Integer d = 200;
           System.out.println(a == b);
           System.out.println(c == d);
       }
   }
   ```

3. **编程题**  
   设计一个 `Shape` 抽象类，包含一个抽象方法 `double area()`。定义两个子类 `Circle` 和 `Rectangle`，分别实现面积计算。编写测试类，利用多态特性，计算多个不同形状的总面积。

4. **改错题**  
   下面的代码希望从文件中读取内容并打印，但存在若干错误或隐患，请修正。
   ```java
   public void readFile(String path) {
       FileReader fr = new FileReader(path);
       String line;
       while ((line = fr.readLine()) != null) {
           System.out.println(line);
       }
   }
   ```

5. **设计题**  
   请使用集合框架实现一个学生管理系统：可以添加学生（学号、姓名、年龄），要求学号唯一；能够按照学号查找学生信息；能够删除指定学号的学生。请写出核心代码。

### 参考答案提示
1. JVM 负责解释执行字节码，字节码与平台无关，不同平台只需实现对应 JVM。JDK 面向开发者，包含 JRE 和工具；JRE 提供运行环境。
2. 输出 `true false`。Integer 在 -128~127 范围内使用缓存，`==` 比较地址，100 在缓存内所以相等，200 超出范围各创建新对象，地址不同。
3. 抽象类 `Shape`，`Circle` 需要半径属性，`Rectangle` 需要长宽；测试类创建 `Shape` 数组，循环调用 `area()`。
4. ① `FileReader` 未处理编译时异常，需 try-catch 或 throws；② `FileReader` 没有 `readLine()` 方法，需包装为 `BufferedReader`；③ 流未关闭，应在 finally 中关闭。
5. 使用 `HashMap<String, Student>`，键为学号，值为学生对象；添加前判断键是否存在；查找用 `get()`，删除用 `remove()`；遍历用 `keySet()` 或 `values()`。

---

## 五、参考资料

1. **《Java 核心技术 卷I》（原书第12版）**  
   Cay S. Horstmann 著，机械工业出版社  
   经典入门书籍，内容翔实，适合零基础，覆盖 Java 14 特性。

2. **《Head First Java》（中文版）**  
   Kathy Sierra 等，中国电力出版社  
   图文并茂，轻松有趣，适合初学者建立编程思维。

3. **Oracle 官方 Java 教程**  
   https://docs.oracle.com/javase/tutorial/  
   权威的在线文档，包含大量范例，随时查阅。

4. **菜鸟教程 - Java 教程**  
   https://www.runoob.com/java/java-tutorial.html  
   中文快速入门资源，知识点简洁清晰，适合速查。

5. **《On Java 8》（中文版）**  
   Bruce Eckel 著，王晨光译（开源社区翻译）  
   深入讲解 Java 8 核心特性，适合有一定基础后进阶。

6. **GitHub 优秀开源项目：Java 编程思想示例代码**  
   https://github.com/BruceEckel/OnJava8-Examples  
   配合《On Java 8》使用的代码示例仓库，可直接运行调试。