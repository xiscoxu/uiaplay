/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.test.uiaplay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Properties;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Monitor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class UiaWindow extends ApplicationWindow {

	static String fileName, folderPath, code;

	public UiaWindow() {
		super(null);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite c = new Composite(parent, SWT.BORDER);

		GridLayout gridLayout = new GridLayout(1, false);
		gridLayout.marginWidth = 0;
		gridLayout.marginHeight = 0;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 0;
		c.setLayout(gridLayout);

		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		c.setLayoutData(gd);
		recordDialog dialog = new recordDialog(Display.getDefault()
				.getActiveShell());
		dialog.open();

		return parent;
	}

	public static void main(String[] args) {
		DebugBridge.init();
		try {
			UiaWindow window = new UiaWindow();
			// window.setBlockOnOpen(true);
			window.open();
			window.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DebugBridge.terminate();
		}
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
	}

	protected Point getInitialLocation(Point initialSize) {
		Composite parent = this.getShell();

		Monitor monitor = parent.getDisplay().getPrimaryMonitor();
		if (parent != null) {
			monitor = parent.getMonitor();
		}
		Rectangle mBounds = monitor.getBounds();
		Point centerPoint = new Point(mBounds.x + mBounds.width / 2, mBounds.y
				+ mBounds.height / 2);

		return new Point(centerPoint.x - (initialSize.x / 2), centerPoint.y
				- (initialSize.y / 2));
	}

	@Override
	protected Point getInitialSize() {
		return new Point(0, 0);

	}

	public class recordDialog extends TitleAreaDialog {
		private Combo packageText;
		private Text classText;
		private Text folderText;
		// private Button addpackage;
		String[] packages = TextHelper.Packages;
		String packagenames = "com.test.autoSanity";
		String className = "SF_888_Lock_Unlock";

		Button OKbutton;
		public final String Default_Info = TextHelper.DefaultInfo;
		boolean OKresult;

		// private String previousPackage;

		protected recordDialog(Shell parentShell) {
			super(parentShell);
		}

		protected Composite createContents(Composite parent) {
			super.createContents(parent);
			this.getShell().setText("UiAutomator编译调试");
			Image image = new Image(this.getShell().getDisplay(),
					"images/uitest.ico");
			this.getShell().setImage(image);
			this.setTitle(TextHelper.RecordCaseTitle);
			this.setMessage(TextHelper.DefaultInfo,
					IMessageProvider.INFORMATION);
			return parent;
		}

		protected Composite createDialogArea(Composite parent) {
			Composite comp1 = (Composite) super.createDialogArea(parent);
			GridLayout comp1Layout = new GridLayout(1, true);
			GridData comp1data = new GridData(GridData.FILL_BOTH);
			comp1.setLayout(comp1Layout);
			comp1.setLayoutData(comp1data);

			Composite comp = new Composite(comp1, SWT.NONE);
			GridLayout layout = new GridLayout(6, true);
			layout.marginTop = 20;
			layout.marginWidth = 30;
			comp.setLayout(layout);
			GridData compdata = new GridData(GridData.FILL_HORIZONTAL);
			comp.setLayoutData(compdata);

			GridData descriptiondata = new GridData(GridData.FILL_HORIZONTAL);
			descriptiondata.horizontalSpan = 4;

			// uitest project folder
			new Label(comp, SWT.NONE).setText(TextHelper.ProjectFolder);
			folderText = new Text(comp, SWT.BORDER | SWT.SINGLE);
			folderText.setLayoutData(descriptiondata);
			folderText.setText(TextHelper.folderDesText);

			Composite editcom = new Composite(comp, SWT.NONE);
			editcom.setLayout(new GridLayout(3, false));
			editcom.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
			final Button edit = new Button(editcom, SWT.CHECK);
			edit.setText("设为默认");
			String[] prop = readLocalProperties();
			if (prop[0].equals("false")) {
				edit.setSelection(false);
			} else {
				edit.setSelection(true);
				folderText.setText(prop[1]);
				folderText.setEnabled(false);
			}

			folderText.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					setMessage(TextHelper.folderDesMsg,
							IMessageProvider.INFORMATION);
				}
			});
			folderText.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					checkDescriptionValid();
				}
			});

			edit.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (edit.getSelection()) {
						folderText.setEnabled(false);
						writeLocalProperties("true", folderText.getText());
					} else {
						folderText.setEnabled(true);
						writeLocalProperties("false", folderText.getText());
					}
				}
			});

			// add package name
			Composite casecomp = new Composite(comp1, SWT.NONE);
			GridLayout caselayout = new GridLayout(1, true);
			casecomp.setLayout(caselayout);
			casecomp.setLayoutData(new GridData(GridData.FILL_BOTH));

			Composite packagecomp = new Composite(casecomp, SWT.None);
			GridLayout packagelayout = new GridLayout(6, true);
			packagelayout.marginWidth = 30;
			packagecomp.setLayout(packagelayout);
			packagecomp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			new Label(packagecomp, SWT.NONE)
					.setText(TextHelper.RecordCasePackage);

			packageText = new Combo(packagecomp, SWT.NONE);
			packageText.setItems(packages);
			packageText.setText(packagenames);
			packageText.setLayoutData(descriptiondata);
			packageText.getText();
			packageText.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					setMessage(TextHelper.RecordCasePackText,
							IMessageProvider.INFORMATION);
				}
			});

			packageText.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					checkPackageValid();
				}
			});

			// add class name
			Composite classcomp = new Composite(casecomp, SWT.None);
			GridLayout classlayout = new GridLayout(6, true);
			classlayout.marginWidth = 30;
			classcomp.setLayout(classlayout);
			classcomp.setLayoutData(compdata);

			new Label(classcomp, SWT.NONE).setText(TextHelper.RecordCaseName);
			classText = new Text(classcomp, SWT.BORDER | SWT.SINGLE);
			classText.setLayoutData(descriptiondata);
			classText.setText(className);

			classText.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					setMessage(TextHelper.RecordCaseRule,
							IMessageProvider.INFORMATION);
				}
			});
			classText.addListener(SWT.Modify, new Listener() {
				@Override
				public void handleEvent(Event e) {
					checkClassValid();
				}
			});

			return parent;
		}

		@SuppressWarnings("unused")
		protected void createButtonsForButtonBar(Composite parent) {
			OKbutton = createButton(parent, recordDialog.OK,
					TextHelper.PlayShell, true);
			Button Cancel = createButton(parent, recordDialog.CANCEL,
					TextHelper.Cancel, false);
		}

		protected void buttonPressed(int buttonId) {
			if (recordDialog.OK == buttonId) {
				String folderPath = folderText.getText();
				String className = packageText.getText() + "."
						+ classText.getText();
				final PlayRecordDialog play = new PlayRecordDialog(
						this.getShell());
				playing(play, folderPath, className);
			}
			if (recordDialog.CANCEL == buttonId)
				close();
		}

		protected void checkClassValid() {
			String classContent = classText.getText();

			if (!classContent.matches("^[a-zA-Z0-9_]*")) {
				setMessage(TextHelper.RecordCaseValid, IMessageProvider.ERROR);
				OKbutton.setEnabled(false);
			} else if (classContent.length() <= 0) {
				setMessage(TextHelper.RecordCaseEmpty, IMessageProvider.ERROR);
				OKbutton.setEnabled(false);
			} else {
				setMessage(TextHelper.RecordCaseRule,
						IMessageProvider.INFORMATION);
				OKbutton.setEnabled(true);
			}
		}

		protected void checkPackageValid() {
			String packageContent = packageText.getText();
			if (!packageContent.matches("^[a-zA-Z.]*")) {
				setMessage(TextHelper.RecordCasePackValid,
						IMessageProvider.ERROR);
				OKbutton.setEnabled(false);
			} else if (packageContent.length() <= 0) {
				setMessage(TextHelper.RecordCasePackEmpty,
						IMessageProvider.ERROR);
				OKbutton.setEnabled(false);
			} else {
				setMessage(TextHelper.RecordCasePackText,
						IMessageProvider.INFORMATION);
				OKbutton.setEnabled(true);
			}
		}

		@SuppressWarnings("unused")
		protected void checkDescriptionValid() {
			String descriptionContent = folderText.getText();
			setMessage(TextHelper.folderDesMsg, IMessageProvider.INFORMATION);
			OKbutton.setEnabled(true);
			// }
		}
	}

	public static String[] readLocalProperties() {
		String filePath = System.getProperty("user.dir")
				+ "\\config.properties";
		String[] values = new String[2];
		String parameterName = "setDefault";
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(filePath);
			// 从输入流中读取属性列表（键和元素对）
			prop.load(fis);
			values[0] = prop.getProperty("setDefault");
			values[1] = prop.getProperty("projectDir");
		} catch (IOException e) {
			System.err.println("Visit " + filePath + " for reading"
					+ parameterName + " value error");
		}
		return values;
	}

	public static void writeLocalProperties(String defaultSettings,
			String projectPath) {
		String filePath = System.getProperty("user.dir")
				+ "\\config.properties";
		String parameterName = "setDefault";
		Properties prop = new Properties();
		try {
			InputStream fis = new FileInputStream(filePath);
			// 从输入流中读取属性列表（键和元素对）
			prop.load(fis);
			// 调用 Hashtable 的方法 put。使用 getProperty 方法提供并行性。
			// 强制要求为属性的键和值使用字符串。返回值是 Hashtable 调用 put 的结果。
			OutputStream fos = new FileOutputStream(filePath);
			prop.setProperty(parameterName, defaultSettings);
			prop.setProperty("projectDir", projectPath);
			// 以适合使用 load 方法加载到 Properties 表中的格式，
			// 将此 Properties 表中的属性列表（键和元素对）写入输出流
			prop.store(fos, "Update '" + parameterName + "' value");
		} catch (IOException e) {
			System.err.println("Visit " + filePath + " for updating "
					+ parameterName + " value error");
		}
	}

	private void playing(final PlayRecordDialog play, String folderPath,
			String className) {
		String projectName = new File(folderPath).getName();
		String path = System.getProperty("user.dir");
		final String build = "cmd /c " + path + "\\ant\\bin\\ant -buildfile "
				+ folderPath + "\\build.xml";
		final String push = "cmd /c " + path + "\\sdk_tools\\adb push "
				+ folderPath + "\\bin\\" + projectName
				+ ".jar /data/local/tmp/";
		final String runCommand = "cmd /c " + path
				+ "\\sdk_tools\\adb shell uiautomator runtest " + projectName
				+ ".jar " + "-c " + className;
		new Thread() {
			public void run() {
				Display.getDefault().syncExec(new Runnable() {
					public void run() {
						play.open();
					}
				});
			}
		}.start();

		new Thread() {
			public void run() {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
				play.playStart();
				Runtime r = Runtime.getRuntime();
				Process p;
				BufferedReader br;
				String inline;
				try {
					p = r.exec(build);
					br = new BufferedReader(new InputStreamReader(
							p.getInputStream(), "utf-8"));
					while (null != (inline = br.readLine())) {
						final String output = inline;
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								System.out.println(output);
							}
						});
					}

					p = r.exec(push);
					br = new BufferedReader(new InputStreamReader(
							p.getInputStream(), "utf-8"));
					while (null != (inline = br.readLine())) {
						final String output = inline;
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								System.out.println(output);
							}
						});
					}
					System.out.println("Push project jar finished!");

					p = r.exec(runCommand);
					br = new BufferedReader(new InputStreamReader(
							p.getInputStream(), "utf-8"));
					while (null != (inline = br.readLine())) {
						final String output = inline;
						Display.getDefault().syncExec(new Runnable() {
							public void run() {
								System.out.println(output);
							}
						});
					}
					play.playEnd();
				} catch (IOException e) {
					System.out.println("数据中断,请重新运行一次");
				}
			}

		}.start();
	}
}
