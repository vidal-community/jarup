package io.github.xhanin.jarup.commands;

import io.github.xhanin.jarup.Command;
import io.github.xhanin.jarup.WorkingCopy;

import java.io.IOException;

/**
 * Date: 10/1/14
 * Time: 18:22
 */
public class CatCommand implements Command<CatCommand> {
    private WorkingCopy workingCopy;
    private String path;
    private String to;
    private String charset;

    public CatCommand() {
    }

    public CatCommand baseOn(WorkingCopy workingCopy) {
        this.workingCopy = workingCopy;
        return this;
    }

    @Override
    public CatCommand parse(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("--encoding=")) {
                withEncoding(arg.substring("--encoding=".length()));
            } else if (arg.startsWith("--to=")) {
                to(arg.substring("--to=".length()));
            } else {
                from(arg);
            }
        }

        if (path == null) {
            throw new IllegalArgumentException("you must provide file to cat");
        }

        return this;
    }

    @Override
    public void execute() throws IOException {
        if (path == null) {
            throw new IllegalStateException("path must be set");
        }
        if (to != null) {
            workingCopy.copyFileTo(path, to);
        } else {
            if (charset == null) {
                charset = workingCopy.getDefaultCharsetFor(path);
            }

            System.out.print(workingCopy.readFile(path, charset));
        }
    }

    public CatCommand from(String path) {
        this.path = path;
        return this;
    }

    public CatCommand to(String path) {
        this.to = path;
        return this;
    }

    public CatCommand withEncoding(String encoding) {
        this.charset = encoding;
        return this;
    }
}
