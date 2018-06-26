package com.jingyue.apktools.utils;

import com.jingyue.apktools.Config;
import com.jingyue.apktools.bean.Direction;
import com.jingyue.apktools.utils.javafx.Delta;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.reflect.misc.MethodUtil;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;

public class ViewUtils {

    public static final String TAG = ViewUtils.class.getSimpleName();

    /**
     * 监听输入并自动保存输入数据
     *
     * @param textField
     * @param key
     */
    public static void listenerInputAndSave(TextField textField, final String key, final boolean isEncrypt) {
        // 监听输入
        textField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    if (isEncrypt) {
                        newValue = com.jingyue.apktools.utils.Base64Utils.encode(newValue.trim());
                    }
                    HistoryUtil.save(key, newValue.trim());
                }
            }
        });
    }

    /**
     * 监听输入并自动保存输入数据
     *
     * @param textField
     * @param key
     */
    public static void listenerInputAndSave(TextField textField, String key) {
        listenerInputAndSave(textField, key, false);
    }

    /**
     * 恢復数据显示
     *
     * @param textField
     * @param key
     */
    public static String review(TextField textField, String key) {
        String value = HistoryUtil.getString(key);
        if (!StringUtils.isEmpty(value)) {
            textField.setText(value);
        }
        return value;
    }

    /**
     * 设置无边界、无标题样式
     *
     * @param primaryStage
     */
    public static void setNoBroder(Stage primaryStage) {
        primaryStage.initStyle(StageStyle.UNDECORATED);
    }

    /**
     * 设置背景图片
     *
     * @param imgPath
     */
    public static void setBackgroudImage(Parent parent, String imgPath) {
        String image = ClassUtils.getResourceAsURL(imgPath).toExternalForm();
        if (image != null) {
            parent.setStyle("-fx-background-image: url('" + image + "'); " +
                    "-fx-background-position: center center; " +
                    "-fx-background-repeat: no-repeat;");
        } else {
            LogUtils.e("'" + imgPath + "' image not found in classpath.");
        }
    }

    /**
     * 设置背景
     *
     * @param pane   pane
     * @param color  颜色
     * @param radii  圆角半径
     * @param insets 矩形区域
     */
    public static void setBackground(Pane pane, Color color, CornerRadii radii, Insets insets) {
        pane.setBackground(new Background(new BackgroundFill(color, radii, insets)));
    }

    /**
     * 设置空背景
     * <p>
     * 已过时，请使用pane.setBackground(Background.EMPTY);
     *
     * @param pane
     */
    @Deprecated
    public static void setEmptyBackground(Pane pane) {
        setBackground(pane, Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY);
    }

    /**
     * 创建一个有光晕效果的矩形光圈
     *
     * @param shadowSize 光晕大小
     * @param r          0-255
     * @param g          0-255
     * @param b          0-255
     * @param a          0-1
     * @return
     */
    // Create a shadow effect as a halo around the pane and not within
    // the pane's content area.
    public static Pane createShadowPane(final int shadowSize, int r, int g, int b, float a) {

        StringBuilder rgbaBuider = new StringBuilder();
        rgbaBuider.append(r).append(",")
                .append(g).append(",")
                .append(b).append(",")
                .append(a);

        final Pane shadowPane = new Pane();
        // a "real" app would do this in a CSS stylesheet.
        shadowPane.setStyle(
                "-fx-background-color: white;" +
                        "-fx-effect: dropshadow(gaussian, rgba(" + rgbaBuider.toString() + "), " + shadowSize + ", 0, 0, 0);" +
                        "-fx-background-insets: " + shadowSize + ";"
        );

        final Rectangle innerRect = new Rectangle();
        final Rectangle outerRect = new Rectangle();
        shadowPane.layoutBoundsProperty().addListener(new ChangeListener<Bounds>() {
            @Override
            public void changed(ObservableValue<? extends Bounds> observable, Bounds oldValue, Bounds newValue) {
                innerRect.relocate(
                        newValue.getMinX() + shadowSize,
                        newValue.getMinY() + shadowSize
                );
                innerRect.setWidth(newValue.getWidth() - shadowSize * 2);
                innerRect.setHeight(newValue.getHeight() - shadowSize * 2);

                outerRect.setWidth(newValue.getWidth());
                outerRect.setHeight(newValue.getHeight());

                Shape clip = Shape.subtract(outerRect, innerRect);
                shadowPane.setClip(clip);
            }
        });

        return shadowPane;
    }


    /**
     * 把一个Node导出为png图片
     *
     * @param node     节点
     * @param saveFile 图片文件
     * @return 是否导出成功
     */
    public static boolean node2Png(Node node, File saveFile) {
        SnapshotParameters parameters = new SnapshotParameters();
        // 背景透明
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = node.snapshot(parameters, null);
        //probably use a file chooser here（原来这里是使用一个文件选择器）
        try {
            return ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", saveFile);
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return false;
    }

    /**
     * 把一个Node导出为png图片
     *
     * @param node     节点
     * @param saveFile 图片文件
     * @param width    宽
     * @param height   高
     * @return 是否导出成功
     */
    public static boolean node2Png(Node node, File saveFile, double width, double height) {
        SnapshotParameters parameters = new SnapshotParameters();
        // 背景透明
        parameters.setFill(Color.TRANSPARENT);
        WritableImage image = node.snapshot(parameters, null);

        // 重置图片大小
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        WritableImage exportImage = imageView.snapshot(parameters, null);

        try {
            return ImageIO.write(SwingFXUtils.fromFXImage(exportImage, null), "png", saveFile);
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return false;
    }

    /**
     * 反射注入字段值
     *
     * @param parent
     * @param controller
     */
    public static void injectFields(Parent parent, Object controller) {
        Class<?> controllerClass = controller.getClass();
        // injectFields
        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node.getId() != null) {
                ReflectUtils.setFieldValue(controllerClass, controller, node.getId(), node);
            }
            if (node instanceof Parent) {
                injectFields((Parent) node, controller);
            }
        }
    }

    /**
     * 强制关联Controller，适用于fxml需要动态关联指定Controller对象的场合，javafx默认关联方式不能解决该问题。
     *
     * @param parent
     * @param controller
     */
    public static void setController(Parent parent, Object controller) {
        try {
            // injectFields
            injectFields(parent, controller);

            if (controller instanceof Initializable) {
                ((Initializable) controller).initialize(null, null);
            } else {
                // Initialize the controller
                Method initializeMethod = controller.getClass().getDeclaredMethod(FXMLLoader.INITIALIZE_METHOD_NAME);
                if (initializeMethod != null) {
                    MethodUtil.invoke(initializeMethod, controller, new Object[]{});
                }
            }
        } catch (NoSuchMethodException e) {
            LogUtils.e(e);
        } catch (IllegalAccessException e) {
            LogUtils.e(e);
        } catch (InvocationTargetException e) {
            LogUtils.e(e);
        }


    }

    /**
     * * 设置窗口icon
     *
     * @param stage
     * @param url   icon的url
     */
    public static void setWindowIcon(Stage stage, URL url) {
        if (url == null) {
            LogUtils.e("setWindowIcon green_icon url is null");
            return;
        }
        stage.getIcons().add(new Image(url.toString()));
    }

    /**
     * 设置窗口icon
     *
     * @param stage
     * @param iconPath icon路径
     */
    public static void setWindowIcon(Stage stage, String iconPath) {
        if (iconPath == null) {
            LogUtils.e("setWindowIcon iconPath is null");
            return;
        }

        File icon = new File(iconPath);
        if (!icon.exists()) {
            LogUtils.e("setWindowIcon green_icon is not exist : " + iconPath);
            return;
        }

        try {
            setWindowIcon(stage, icon.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            LogUtils.e(e);
        }
    }

    /**
     * 最大化
     *
     * @param stage
     */
    private static boolean flag = false;
    public static void fullscreen(Stage stage, Button btn) {
        btn.getStyleClass().clear();
        if(flag){
            btn.getStyleClass().add("button_fullscreen");
            stage.setWidth(Config.WINDOW_WIDTH);
            stage.setHeight(Config.WINDOW_HEIGHT);
            stage.centerOnScreen();
            flag = false;
        }else {
            btn.getStyleClass().add("button_unfullscreen");
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            stage.setX(primaryScreenBounds.getMinX());
            stage.setY(primaryScreenBounds.getMinY());
            stage.setWidth(primaryScreenBounds.getWidth());
            stage.setHeight(primaryScreenBounds.getHeight());
            flag = true;
        }
    }

    /**
     * 打开一个新窗口
     *
     * @param fxmlUrl     fxml文件的url
     * @param isShowTitle 是否显示title
     * @return Stage，如果出现异常返回null
     */
    public static Stage newWindow(URL fxmlUrl, boolean isShowTitle) {
        try {
            Stage stage = new Stage();
            if (!isShowTitle) {
                setNoBroder(stage);
            }
            // 背景透明
            stage.initStyle(StageStyle.TRANSPARENT);
            Parent layout = FXMLLoader.load(fxmlUrl);

            Scene scene = new Scene(layout, Color.TRANSPARENT);
            stage.setScene(scene);

            // 在屏幕中间
            stage.centerOnScreen();

            return stage;
        } catch (IOException e) {
            LogUtils.e(e);
        }
        return null;
    }

    /**
     * 注册拖拽事件
     *
     * @param stage
     * @param root
     */
    public static void registerDragEvent(final Stage stage, final Node root,final boolean resizable) {
        final int border = 4;
        final Direction dc = new Direction();
        root.setOnMouseMoved(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(stage.isFullScreen()){
                    return;
                }

                Scene scene = stage.getScene();
                double mouseEventX = event.getSceneX();
                double mouseEventY = event.getSceneY();
                double sceneWidth = scene.getWidth();
                double sceneHeight = scene.getHeight();
                Cursor cursorType = Cursor.DEFAULT;// 鼠标光标初始为默认类型，若未进入调整窗口状态，保持默认类型
                if(resizable){
                    if (mouseEventX < border && mouseEventY < border) {//左上角
                        cursorType = Cursor.NW_RESIZE;
                        dc.direction = Direction.LEFT_TOP;
                    } else if (mouseEventX < border && mouseEventY > sceneHeight - border) {//左下角
                        cursorType = Cursor.SW_RESIZE;
                        dc.direction = Direction.LEFT_BOTTOM;
                    } else if (mouseEventX > sceneWidth - border && mouseEventY < border) {//右上
                        cursorType = Cursor.NE_RESIZE;
                        dc.direction = Direction.RIGHT_TOP;
                    } else if (mouseEventX > sceneWidth - border && mouseEventY > sceneHeight - border) {//右下
                        cursorType = Cursor.SE_RESIZE;
                        dc.direction = Direction.RIGHT_BOTTOM;
                    } else if (mouseEventX < border) {//左
                        dc.direction = Direction.LEFT;
                        cursorType = Cursor.W_RESIZE;
                    } else if (mouseEventX > sceneWidth - border) {//右
                        dc.direction = Direction.RIGHT;
                        cursorType = Cursor.E_RESIZE;
                    } else if (mouseEventY < border) {//上
                        dc.direction = Direction.TOP;
                        cursorType = Cursor.N_RESIZE;
                    } else if (mouseEventY > sceneHeight - border) {//下
                        dc.direction = Direction.BOTTOM;
                        cursorType = Cursor.S_RESIZE;
                    } else {
                        cursorType = Cursor.DEFAULT;
                        dc.direction = Direction.MOVE;
                    }
                }else {
                    cursorType = Cursor.DEFAULT;
                }
                root.setCursor(cursorType);
            }
        });

        final Delta drag = new Delta();//记录左上角位置
        final Delta dragDelta = new Delta();//记录拖动距离
        root.setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(stage.isFullScreen()){
                    return;
                }
                dragDelta.x=event.getScreenX();
                dragDelta.y=event.getScreenY();
                drag.x = stage.getX() - event.getScreenX();
                drag.y = stage.getY() - event.getScreenY();
            }
        });
        root.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if(stage.isFullScreen()){
                    return;
                }
                if(resizable){
                    if(dc.direction==Direction.MOVE){
                        stage.setX(event.getScreenX() + drag.x);
                        stage.setY(event.getScreenY() + drag.y);
                    }else {
                        double x = event.getScreenX();
                        double y = event.getScreenY();
                        double nextX = stage.getX();
                        double nextY = stage.getY();
                        double nextWidth = stage.getWidth();
                        double nextHeight = stage.getHeight();
                        if(dc.direction==Direction.MOVE){
                            stage.setX(event.getSceneX() + drag.x);
                            stage.setY(event.getSceneY() + drag.y);
                        }else {
                            if(dc.direction==Direction.BOTTOM||dc.direction==Direction.LEFT_BOTTOM||dc.direction==Direction.RIGHT_BOTTOM){
                                nextHeight = y-nextY;
                            }
                            if(dc.direction==Direction.RIGHT||dc.direction==Direction.RIGHT_BOTTOM||dc.direction==Direction.RIGHT_TOP){
                                nextWidth = x-nextX;
                            }
                            if(dc.direction==Direction.TOP||dc.direction==Direction.RIGHT_TOP||dc.direction==Direction.LEFT_TOP){
                                double lastB = nextY+nextHeight;
                                nextHeight = lastB-y-drag.y;
                                if ( nextHeight > Config.WINDOW_HEIGHT) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                                    nextY = y + drag.y;
                                }
                            }
                            if(dc.direction==Direction.LEFT||dc.direction==Direction.LEFT_TOP||dc.direction==Direction.LEFT_BOTTOM){
                                double lastR = nextX+nextWidth;
                                nextWidth = lastR-x-drag.x;
                                if ( nextWidth > Config.WINDOW_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                                    nextX = x+drag.x;
                                }
                            }

                            if ( nextWidth <= Config.WINDOW_WIDTH) {// 如果窗口改变后的宽度小于最小宽度，则宽度调整到最小宽度
                                nextWidth = Config.WINDOW_WIDTH;
                            }
                            if (nextHeight <= Config.WINDOW_HEIGHT) {// 如果窗口改变后的高度小于最小高度，则高度调整到最小高度
                                nextHeight = Config.WINDOW_HEIGHT;
                            }
                            stage.setX(nextX);
                            stage.setY(nextY);
                            stage.setWidth(nextWidth);
                            stage.setHeight(nextHeight);
                        }
                    }
                }else {
                    stage.setX(event.getScreenX() + drag.x);
                    stage.setY(event.getScreenY() + drag.y);
                }
            }
        });
    }

}
