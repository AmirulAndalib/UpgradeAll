package net.xzos.UpgradeAll.gson;

public class HubConfig {
    /**
     * base_version : 1
     * uuid :
     * info : {"config_name":"","config_version":""}
     * web_crawler : {"user_agent":"","app_config":{"default_name":{"text":"","search_path":{"xpath":"","regex":""}},"release":{"release_node":"","attribute":{"version_number":{"text":"","search_path":{"xpath":"","regex":""}},"assets":{"file_name":{"text":"","search_path":{"xpath":"","regex":""}},"download_url":{"text":"","search_path":{"xpath":"","regex":""}}}}}}}
     */

    private String uuid;
    private int base_version;
    private InfoBean info;
    private WebCrawlerBean web_crawler;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getBaseVersion() {
        return base_version;
    }

    public void setBaseVersion(int config_version) {
        this.base_version = config_version;
    }

    public InfoBean getInfo() {
        return info;
    }

    public void setInfo(InfoBean info) {
        this.info = info;
    }

    public WebCrawlerBean getWebCrawler() {
        return web_crawler;
    }

    public void setWebCrawler(WebCrawlerBean web_crawler) {
        this.web_crawler = web_crawler;
    }

    public static class InfoBean {
        /**
         * config_name :
         * config_version :
         */

        private String config_name;
        private String config_version;

        public String getConfigName() {
            return config_name;
        }

        public void setConfigName(String config_name) {
            this.config_name = config_name;
        }

        public String getConfigVersion() {
            return config_version;
        }

        public void setConfigVersion(String config_version) {
            this.config_version = config_version;
        }
    }

    public static class WebCrawlerBean {
        /**
         * user_agent :
         * app_config : {"default_name":{"text":"","search_path":{"xpath":"","regex":""}},"release":{"release_node":"","attribute":{"version_number":{"text":"","search_path":{"xpath":"","regex":""}},"assets":{"file_name":{"text":"","search_path":{"xpath":"","regex":""}},"download_url":{"text":"","search_path":{"xpath":"","regex":""}}}}}}
         */

        private String tool;
        private String user_agent;
        private AppConfigBean app_config;

        public String getTool() {
            return tool;
        }

        public void setTool(String tool) {
            this.tool = tool;
        }

        public String getUserAgent() {
            return user_agent;
        }

        public void setUserAgent(String user_agent) {
            this.user_agent = user_agent;
        }

        public AppConfigBean getAppConfig() {
            return app_config;
        }

        public void setAppConfig(AppConfigBean app_config) {
            this.app_config = app_config;
        }

        public static class AppConfigBean {
            /**
             * default_name : {"text":"","search_path":{"xpath":"","regex":""}}
             * release : {"release_node":"","attribute":{"version_number":{"text":"","search_path":{"xpath":"","regex":""}},"assets":{"file_name":{"text":"","search_path":{"xpath":"","regex":""}},"download_url":{"text":"","search_path":{"xpath":"","regex":""}}}}}
             */

            private StringItemBean default_name;
            private ReleaseBean release;

            public StringItemBean getDefaultName() {
                return default_name;
            }

            public void setDefaultName(StringItemBean default_name) {
                this.default_name = default_name;
            }

            public ReleaseBean getRelease() {
                return release;
            }

            public void setRelease(ReleaseBean release) {
                this.release = release;
            }


            public static class ReleaseBean {
                /**
                 * release_node :
                 * attribute : {"version_number":{"text":"","search_path":{"xpath":"","regex":""}},"assets":{"file_name":{"text":"","search_path":{"xpath":"","regex":""}},"download_url":{"text":"","search_path":{"xpath":"","regex":""}}}}
                 */

                private String release_node;
                private AttributeBean attribute;

                public String getReleaseNode() {
                    return release_node;
                }

                public void setReleaseNode(String release_node) {
                    this.release_node = release_node;
                }

                public AttributeBean getAttribute() {
                    return attribute;
                }

                public void setAttribute(AttributeBean attribute) {
                    this.attribute = attribute;
                }

                public static class AttributeBean {
                    /**
                     * version_number : {"text":"","search_path":{"xpath":"","regex":""}}
                     * assets : {"file_name":{"text":"","search_path":{"xpath":"","regex":""}},"download_url":{"text":"","search_path":{"xpath":"","regex":""}}}
                     */

                    private StringItemBean version_number;
                    private AssetsBean assets;

                    public StringItemBean getVersion_number() {
                        return version_number;
                    }

                    public void setVersion_number(StringItemBean version_number) {
                        this.version_number = version_number;
                    }

                    public AssetsBean getAssets() {
                        return assets;
                    }

                    public void setAssets(AssetsBean assets) {
                        this.assets = assets;
                    }

                    public static class AssetsBean {
                        /**
                         * file_name : {"text":"","search_path":{"xpath":"","regex":""}}
                         * download_url : {"text":"","search_path":{"xpath":"","regex":""}}
                         */

                        private StringItemBean file_name;
                        private DownloadItemBean download_url;

                        public StringItemBean getFileName() {
                            return file_name;
                        }

                        public void setFile_name(StringItemBean file_name) {
                            this.file_name = file_name;
                        }

                        public DownloadItemBean getDownloadUrl() {
                            return download_url;
                        }

                        public void setDownloadUrl(DownloadItemBean download_url) {
                            this.download_url = download_url;
                        }
                    }
                }
            }
        }
    }

    public static class StringItemBean {
        /**
         * text :
         * search_path : {"xpath":"","regex":""}
         */

        private String text;
        private SearchPathBean search_path;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public SearchPathBean getSearchPath() {
            return search_path;
        }

        public void setSearchPath(SearchPathBean search_path) {
            this.search_path = search_path;
        }

        public static class SearchPathBean {
            /**
             * xpath :
             * regex :
             */

            private String xpath;
            private String regex;

            public String getXpath() {
                return xpath;
            }

            public void setXpath(String xpath) {
                this.xpath = xpath;
            }

            public String getRegex() {
                return regex;
            }

            public void setRegex(String regex) {
                this.regex = regex;
            }
        }
    }

    public static class DownloadItemBean {
        /**
         * text :
         * search_path : {"xpath":"","regex":""}
         */

        private String text;
        private SearchPathBean search_path;

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public SearchPathBean getSearchPath() {
            return search_path;
        }

        public void setSearchPath(SearchPathBean search_path) {
            this.search_path = search_path;
        }

        public static class SearchPathBean {
            /**
             * is_button:
             * xpath:
             * regex:
             */

            private String xpath;
            private String regex;
            private boolean is_button;

            public String getXpath() {
                return xpath;
            }

            public void setXpath(String xpath) {
                this.xpath = xpath;
            }

            public String getRegex() {
                return regex;
            }

            public void setRegex(String regex) {
                this.regex = regex;
            }

            public boolean getIsButton() {
                return is_button;
            }

            public void setIsButton(boolean is_button) {
                this.is_button = is_button;
            }
        }
    }
}