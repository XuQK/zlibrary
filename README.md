[TOC]

# zlibrary mvvm-databinding 基础开发模块说明

本模块与 Android 的 databinding 功能，RxJava2，Retrofit2 等库深度结合，是一个我自己理解的 mvvm 开发框架。

Activity 或 Fragment 须绑定对应的 ZViewModel 子类，作为其数据持有类，持有其一切数据，ZViewModel 交由其宿主 Activity 的 FragmentManager 进行管理，避免由于设备信息改变，如屏幕旋转中 Activity 的销毁重建导致的 ViewModel 重建失去数据。

ZViewModel 不持有宿主的 Activity Context，防止内存泄漏，一切需要 Activity Context 的操作，皆可在对应的 ZViewModel 子类中添加 BaseNavigator 子接口，由宿主 Activity/Fragment 实现该接口进行调用。

## basekit

### cache

基础的SP存储工具。

### dialog

基础的 DialogFragment 工具，用于项目统一弹窗，源码基本上来自[NiceDialog开源库](https://github.com/Othershe/NiceDialog)，其中另外添加了监听返回键的接口，以便在 Dialog 可见时，点击 BACK 按键可以进行一些操作。

### zlog

简单的日志格式化工具，源码来自[xDroid快速开发框架开源项目](https://github.com/limedroid/XDroid)。

### Kits工具类

相关便利工具，源码来自[xDroid快速开发框架开源项目](https://github.com/limedroid/XDroid)。

## basenet

统一的网络请求库，包含一些基本的网络设定，采用`Retrofit2+RxJava`的方式进行网络请求。

## baseui

### definedView

计划用来放置项目通用的自定义View

#### ActivityUtils类

来源于[Google的示范项目todo-mvvm-databinding](https://github.com/googlesamples/android-architecture/tree/todo-mvvm-databinding)，统一管理 Fragment 的 add，在本模块中是统一管理 ViewModel 的加载。

#### BaseNavigator接口

接口类，方便 ViewModel 进行一些需要 Activity Context 的操作，比如 UI 变换相关的操作
使用方法：ViewModel里需要持有该接口的子接口，宿主Activity/Fragment实现它后，即可在 ViewModel 里调用定义在 BaseNavigator 里的宿主的相关方法。
目前定义了一些三类方法：

- 启动和结束 Activity。
- 展示 Dialog，包括“加载中Dialog”，“确认取消Dialog”，以及可启动简单的自定义Dialog。
- 调用 RxPermission 进行单个权限申请的方法。

#### BaseViewModel

ViewModel 基类，此为 Activity/Fragment 的数据持有类，与其宿主完全解耦，其持有的 Context 为 ApplicationContext，防止内存泄漏。其子类必须实现基本构造方法，在基类 Activity/Fragment 中默认通过反射创建其实例。
ViewModel 通过 ViewModelHolder，交由宿主 Activity 的 FragmentManager 进行统一管理，通过宿主的 `getViewModelTag()` 获取其在 FragmentManager 中的标签。
主要功能如下：

- `init()`方法默认只在 ViewModel 构造的时候，即 Activity/Fragment 的`onCreate()/onCreateView()`方法中进行一次调用。
- 若要使用 RxBus，须重写`useRxBus()`方法，返回`true`。
- 实现了在宿主 Activity/Fragment 中展示和隐藏“加载中Dialog”的方法，显示“确认/取消Dialog”的方法。
- `bindToLifeCycle()`是给 RxJava 使用，防止其内存泄漏。

### UiCallback

Activity 和 Fragment 的统一行为定义接口

### ZActivity/ZFragment/ZLazyFragment

Activity/Fragment/LazyFragment 基类，须与对应的 ViewModel 配套使用，主要实现了以下通用功能：

- 在`onCreate()/onCreateView()`中主要实现了获取配套的 ViewModel 实例。
- 实现了定义在 BaseNavigator 和 UiCallback 两个接口中的方法，方便 ViewModel 进行调用。

---

非常感谢[xDroid快速开发框架开源项目](https://github.com/limedroid/XDroid)。最初开始进行开发的时候就是使用的该项目，本项目基本上也是由该项目启发而来，并借鉴了该项目的思路和实现方式，使用用了该项目部分源码。