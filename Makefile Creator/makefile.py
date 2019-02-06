'Makefile generator package'
import os


class Makefile(object):
    """ Makefile generator class """

    current_directory = None
    path = None
    sources = []
    project_name = ""
    HEADERS = {}

    def __init__(self, path, sources, HEADERS):
        self.path = path
        self.sources = sources
        self.HEADERS = HEADERS
        self.current_directory = os.getcwd()
        self.project_name = self.path.rsplit('/', 1)[1]

        self.include_paths = []

    def generate(self):
        """ Method for generating Makefile file """

        mkfile = open(self.path + "/Makefile", "w")
        mkfile.write(self.project_name + ":")

        compiled_objects = ""

        for inc in self.sources.items():
            if inc[0].endswith(".c"):
                compiled_objects += " " + \
                    inc[0].rsplit('/', 1)[1].replace(".c", ".o")

        mkfile.write(compiled_objects)
        mkfile.write("\n\tgcc" + compiled_objects + " -o " + self.project_name)

        for inc in self.sources.items():
            if inc[0].endswith(".c"):
                filename = inc[0].rsplit('/', 1)[1]
                # Object declaration
                mkfile.write("\n\n" + filename.replace(".c", ".o") + ": ")
                mkfile.write(inc[0].replace(
                    self.path + "/", "") + " ")  # source file

                for includes in inc[1]:  # headers needed for compilation
                    if includes not in self.HEADERS: # Check if file exists in headers dictionary
                        raise Exception("File not found for include", includes)

                    mkfile.write(self.HEADERS[includes] + " ")

                    include_path = os.path.dirname(self.HEADERS[includes])
                    if include_path not in self.include_paths:
                        self.include_paths.append(include_path)

                mkfile.write("\n\tgcc -c")  # Command header

                for path in self.include_paths:
                    mkfile.write(" -I " + path)

                # The source file
                mkfile.write(" " + inc[0].replace(self.path + "/", ""))

        mkfile.write("\n\nclean:\n\trm -f " +
                     compiled_objects + " " + self.project_name)
        mkfile.close()