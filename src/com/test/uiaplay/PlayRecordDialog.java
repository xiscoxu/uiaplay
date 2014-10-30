package com.test.uiaplay;

import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class PlayRecordDialog extends Dialog implements PlayRecordCallback {

	public Text playtext;
	Button confirm;

	protected PlayRecordDialog(Shell parentShell) {
		super(parentShell);
	}

	// protected int getShellStyle() {
	// // 窗口带标题栏和边框而且无任何按钮　SWT.APPLICATION_MODAL设置为模式化窗体
	// return SWT.TITLE;
	// }

	protected Control createDialogArea(Composite parent) {

		Composite shell = (Composite) super.createDialogArea(parent);
		return shell;

	}

	// 设置对话框初始大小

	protected Point getInitialSize() {

		return new Point(600, 400);

	}

	// 设置初始位置
	// protected Point getInitialLocation() {
	// return new Point(500, 100);
	// }

	// 设置对话框窗口标题和图标

	protected void configureShell(final Shell newShell) {

		super.configureShell(newShell);
		GridLayout gd = new GridLayout(1, false);
		newShell.setLayout(gd);

		newShell.setText(TextHelper.PlayShell);
		Text playtext = createText(newShell, SWT.MULTI | SWT.WRAP
				| SWT.V_SCROLL | SWT.H_SCROLL | SWT.MAX);

		// 输出重定向设置
		MyPrintStream mps = new MyPrintStream(System.out, playtext);
		System.setOut(mps);
		System.setErr(mps);

		confirm = createButton(newShell, SWT.TOGGLE, TextHelper.PlayOnGoing);
		confirm.setEnabled(false);
	}

	protected Text createText(Composite parent, int id) {

		Text text = new Text(parent, id);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.FILL_VERTICAL);
		data.horizontalSpan = 50;
		data.verticalSpan = 65;
		text.setLayoutData(data);

		return text;
	}

	protected Button createButton(Composite parent, int id, String label) {
		Composite forBut = new Composite(parent, SWT.NONE);
		GridLayout gd = new GridLayout(1, false);
		forBut.setLayout(gd);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_CENTER);
		forBut.setLayoutData(data);

		Button but = new Button(forBut, id);
		but.setText(label);
		return but;

	}

	protected void createButtonsForButtonBar(Composite parent) {

	}

	public void playStart() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				confirm.setText(TextHelper.PlayOnGoing);
				confirm.setEnabled(false);
			}
		});
	}

	public void playEnd() {
		Display.getDefault().syncExec(new Runnable() {
			public void run() {
				confirm.setText(TextHelper.PlayDone);
				confirm.setEnabled(true);
				confirm.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						getShell().dispose();
					}
				});
			}
		});
	}

	// 定义一个PrintStream子类,将打印语句输出流重定向到Text组件中显示
	class MyPrintStream extends PrintStream {

		private Text text;

		public MyPrintStream(OutputStream out, Text text) {
			super(out);
			this.text = text;
		}

		// 重写父类write方法,这个方法是所有打印方法里面都要调用的方法
		public void write(byte[] buf, int off, int len) {
			final String message = new String(buf, off, len);

			// SWT非界面线程访问组件的方法
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					// 把信息添加到组件中
					if (text != null && !text.isDisposed()) {
						text.append(message);
					}
				}
			});

		}

	}

}
